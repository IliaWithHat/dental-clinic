package org.ilia.appointmentservice.service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.ilia.appointmentservice.controller.request.CreateAppointmentDto;
import org.ilia.appointmentservice.controller.request.DateRangeDto;
import org.ilia.appointmentservice.controller.request.UpdateAppointmentDto;
import org.ilia.appointmentservice.controller.response.AppointmentDto;
import org.ilia.appointmentservice.entity.Appointment;
import org.ilia.appointmentservice.enums.Role;
import org.ilia.appointmentservice.enums.State;
import org.ilia.appointmentservice.exception.*;
import org.ilia.appointmentservice.feign.TimeServiceClient;
import org.ilia.appointmentservice.feign.UserServiceClient;
import org.ilia.appointmentservice.feign.response.UserDto;
import org.ilia.appointmentservice.feign.response.WorkingTimeDto;
import org.ilia.appointmentservice.mapper.AppointmentMapper;
import org.ilia.appointmentservice.repository.AppointmentRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.DefaultOAuth2AuthenticatedPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

import static lombok.AccessLevel.PRIVATE;
import static org.ilia.appointmentservice.constant.ExceptionMessages.*;
import static org.ilia.appointmentservice.enums.Role.DOCTOR;
import static org.ilia.appointmentservice.enums.Role.PATIENT;
import static org.ilia.appointmentservice.enums.State.FREE;
import static org.ilia.appointmentservice.enums.State.OCCUPIED;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
@Transactional
public class AppointmentService {

    AppointmentRepository appointmentRepository;
    AppointmentMapper appointmentMapper;
    TimeServiceClient timeServiceClient;
    UserServiceClient userServiceClient;
    MailService mailService;

    public AppointmentDto create(Role role, UUID doctorId, CreateAppointmentDto appointmentToSave) {
        UserDto doctor = verifyUserExistByRoleAndId(role, doctorId);
        verifyDoctorIsWorking(doctor);
        verifyAppointmentNotExistByIdAndDate(doctorId, appointmentToSave.getDate());
        verifyAppointmentIsFree(doctorId, appointmentToSave.getDate());

        Appointment convertedAppointment = appointmentMapper.toAppointment(appointmentToSave, getCurrentUserId(), doctorId);
        Appointment savedAppointment = appointmentRepository.save(convertedAppointment);

        UserDto patient = userServiceClient.findById(PATIENT, savedAppointment.getPatientId());
        mailService.sendAppointmentConfirmationEmail(doctor, patient, savedAppointment);

        return appointmentMapper.toAppointmentDto(savedAppointment);
    }

    public AppointmentDto update(Role role, UUID doctorId, UUID appointmentId, UpdateAppointmentDto appointmentToUpdate) {
        verifyUserExistByRoleAndId(role, doctorId);

        return appointmentRepository.findByIdAndDoctorId(appointmentId, doctorId)
                .map(appointment -> appointmentMapper.updateAppointment(appointmentToUpdate, appointment))
                .map(appointmentRepository::save)
                .map(appointmentMapper::toAppointmentDto)
                .orElseThrow(() -> new AppointmentNotFoundException(APPOINTMENT_NOT_FOUND + appointmentId));
    }

    public AppointmentDto findById(Role role, UUID doctorId, UUID appointmentId) {
        verifyUserExistByRoleAndId(role, doctorId);

        return appointmentRepository.findByIdAndDoctorId(appointmentId, doctorId)
                .map(appointmentMapper::toAppointmentDto)
                .orElseThrow(() -> new AppointmentNotFoundException(APPOINTMENT_NOT_FOUND + appointmentId));
    }

    public List<AppointmentDto> find(Role role, UUID userId, DateRangeDto dateRangeDto, State state) {
        verifyUserExistByRoleAndId(role, userId);

        if (role == PATIENT && state == OCCUPIED) {
            List<Appointment> appointments;
            if (dateRangeDto.equals(new DateRangeDto(null, null))) {
                appointments = appointmentRepository.findByPatientId(userId);
            } else {
                appointments = appointmentRepository.findByPatientIdAndDateRange(
                        userId, dateRangeDto.getFrom().atStartOfDay(), dateRangeDto.getTo().atStartOfDay());
            }
            return appointments.stream()
                    .map(appointmentMapper::toAppointmentDto)
                    .toList();
        } else if (role == DOCTOR && state == OCCUPIED) {
            return appointmentRepository.findByDoctorIdAndDateRange(
                            userId, dateRangeDto.getFrom().atStartOfDay(), dateRangeDto.getTo().atStartOfDay()).stream()
                    .map(appointmentMapper::toAppointmentDto)
                    .toList();
        } else if (role == DOCTOR && state == FREE) {
            List<WorkingTimeDto> doctorWorkingTimes = timeServiceClient.findWorkingTimesByDoctorId(role, userId);
            if (doctorWorkingTimes.isEmpty()) {
                return Collections.emptyList();
            }

            List<Appointment> occupiedAppointments = appointmentRepository.findByDoctorIdAndDateRange(
                    userId, dateRangeDto.getFrom().atStartOfDay(), dateRangeDto.getTo().atStartOfDay());

            return generateFreeDates(doctorWorkingTimes, occupiedAppointments, dateRangeDto).stream()
                    .map(date -> appointmentMapper.toAppointmentDto(date, userId))
                    .toList();
        }
        throw new RuntimeException();
    }

    private List<LocalDateTime> generateFreeDates(List<WorkingTimeDto> doctorWorkingTimes, List<Appointment> occupiedAppointments, DateRangeDto dateRangeDto) {
        List<LocalDateTime> freeDates = new ArrayList<>();
        Map<DayOfWeek, WorkingTimeDto> workingTimesMap = initializeWorkingTimeMap(doctorWorkingTimes);

        for (LocalDate currentDate = dateRangeDto.getFrom();
             currentDate.isBefore(dateRangeDto.getTo());
             currentDate = currentDate.plusDays(1)) {
            WorkingTimeDto workingTimeForDay = workingTimesMap.get(currentDate.getDayOfWeek());
            if (workingTimeForDay != null) {
                LocalTime startTime = workingTimeForDay.getStartTime();
                LocalTime endTime = workingTimeForDay.getEndTime();
                LocalTime breakStartTime = workingTimeForDay.getBreakStartTime();
                LocalTime breakEndTime = workingTimeForDay.getBreakEndTime();
                Integer interval = workingTimeForDay.getTimeIntervalInMinutes();

                if (breakStartTime == null && breakEndTime == null) {
                    addTimesToList(freeDates, currentDate, interval, startTime, endTime);
                } else {
                    addTimesToList(freeDates, currentDate, interval, startTime, breakStartTime);
                    addTimesToList(freeDates, currentDate, interval, breakEndTime, endTime);
                }
            }
        }

        Set<LocalDateTime> occupiedDates = occupiedAppointments.stream()
                .map(Appointment::getDate)
                .collect(Collectors.toSet());

        return freeDates.stream()
                .filter(date -> !occupiedDates.contains(date))
                .toList();
    }

    private Map<DayOfWeek, WorkingTimeDto> initializeWorkingTimeMap(List<WorkingTimeDto> workingTimeDtoList) {
        return workingTimeDtoList.stream().collect(Collectors.toMap(WorkingTimeDto::getDay, t -> t));
    }

    private void addTimesToList(List<LocalDateTime> freeDates, LocalDate currentDate, Integer interval, LocalTime startTime, LocalTime endTime) {
        for (LocalTime currentTime = startTime;
             currentTime.isBefore(endTime);
             currentTime = currentTime.plusMinutes(interval)) {
            freeDates.add(LocalDateTime.of(currentDate, currentTime));
        }
    }

    public void delete(Role role, UUID doctorId, UUID appointmentId) {
        verifyUserExistByRoleAndId(role, doctorId);
        Appointment appointment = verifyAppointmentExistById(appointmentId);
        verifyAppointmentNotCompleted(appointment);

        appointmentRepository.deleteById(appointmentId);
    }

    private UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return UUID.fromString(((DefaultOAuth2AuthenticatedPrincipal) authentication.getPrincipal()).getName());
    }

    private UserDto verifyUserExistByRoleAndId(Role role, UUID id) {
        UserDto user = userServiceClient.findById(role, id);
        if (user.getRole() != role) {
            throw new UserNotFoundException(USER_NOT_FOUND_BY_ID_AND_ROLE.formatted(id, role));
        }
        return user;
    }

    private void verifyDoctorIsWorking(UserDto doctor) {
        if (!doctor.getIsWorking()) {
            throw new DoctorNotWorkingException(DOCTOR_NOT_WORKING);
        }
    }

    private void verifyAppointmentNotCompleted(Appointment appointment) {
        if (appointment.getIsPatientCome() != null) {
            throw new CompletedAppointmentDeletionException(COMPLETED_APPOINTMENT_DELETION);
        }
    }

    private Appointment verifyAppointmentExistById(UUID appointmentId) {
        Optional<Appointment> appointment = appointmentRepository.findById(appointmentId);
        if (appointment.isEmpty()) {
            throw new AppointmentNotFoundException(APPOINTMENT_NOT_FOUND + appointmentId);
        }
        return appointment.get();
    }

    private void verifyAppointmentNotExistByIdAndDate(UUID doctorId, LocalDateTime appointmentDate) {
        if (appointmentRepository.findByDoctorIdAndDate(doctorId, appointmentDate).isPresent()) {
            throw new AppointmentAlreadyExistException(APPOINTMENT_ALREADY_EXIST);
        }
    }

    private void verifyAppointmentIsFree(UUID doctorId, LocalDateTime appointmentDate) {
        LocalDate date = appointmentDate.toLocalDate();
        DateRangeDto dateRangeDto = new DateRangeDto(date, date.plusDays(1));
        List<LocalDateTime> freeDates = find(DOCTOR, doctorId, dateRangeDto, FREE).stream()
                .map(AppointmentDto::getDate)
                .toList();
        if (!freeDates.contains(appointmentDate)) {
            throw new InvalidAppointmentDateException(INVALID_APPOINTMENT_DATE);
        }
    }
}
