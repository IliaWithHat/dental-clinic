package org.ilia.appointmentservice.service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.ilia.appointmentservice.controller.request.CreateAppointmentDto;
import org.ilia.appointmentservice.controller.request.DateRangeDto;
import org.ilia.appointmentservice.controller.request.UpdateAppointmentDto;
import org.ilia.appointmentservice.controller.response.AppointmentDto;
import org.ilia.appointmentservice.entity.Appointment;
import org.ilia.appointmentservice.entity.MailDetails;
import org.ilia.appointmentservice.enums.Role;
import org.ilia.appointmentservice.enums.State;
import org.ilia.appointmentservice.exception.AppointmentAlreadyExistException;
import org.ilia.appointmentservice.exception.AppointmentNotFoundException;
import org.ilia.appointmentservice.exception.DoctorNotWorkingException;
import org.ilia.appointmentservice.exception.UserNotFoundException;
import org.ilia.appointmentservice.feign.TimeServiceClient;
import org.ilia.appointmentservice.feign.UserServiceClient;
import org.ilia.appointmentservice.feign.response.UserDto;
import org.ilia.appointmentservice.feign.response.WorkingTimeDto;
import org.ilia.appointmentservice.kafka.KafkaProducer;
import org.ilia.appointmentservice.mapper.AppointmentMapper;
import org.ilia.appointmentservice.repository.AppointmentRepository;
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
import static org.ilia.appointmentservice.enums.Subject.APPOINTMENT_CONFIRMATION;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
@Transactional
public class AppointmentService {

    AppointmentRepository appointmentRepository;
    AppointmentMapper appointmentMapper;
    TimeServiceClient timeServiceClient;
    UserServiceClient userServiceClient;
    KafkaProducer kafkaProducer;

    public AppointmentDto create(Role role, UUID doctorId, CreateAppointmentDto appointmentToSave) {
        UserDto doctor = verifyUserExistByRoleAndId(role, doctorId);
        verifyDoctorIsWorking(doctor);
        verifyAppointmentNotExistByIdAndDate(doctorId, appointmentToSave.getDate());

        Appointment savedAppointment = appointmentRepository.save(appointmentMapper.toAppointment(appointmentToSave));
        sendEmailToPatientWithAppointmentConfirmation(savedAppointment);
        return appointmentMapper.toAppointmentDto(savedAppointment);
    }

    private void sendEmailToPatientWithAppointmentConfirmation(Appointment appointment) {
        UserDto doctor = userServiceClient.findById(DOCTOR, appointment.getDoctorId());
        UserDto patient = userServiceClient.findById(PATIENT, appointment.getPatientId());

        MailDetails mailDetails = MailDetails.builder()
                .subject(APPOINTMENT_CONFIRMATION)
                .patientEmail(patient.getEmail())
                .patientFirstName(patient.getFirstName())
                .patientLastName(patient.getLastName())
                .doctorFirstName(doctor.getFirstName())
                .doctorLastName(doctor.getLastName())
                .appointmentDate(appointment.getDate())
                .build();

        kafkaProducer.send(mailDetails);
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
            List<Appointment> appointments = appointmentRepository.findByPatientIdAndDateRange(
                    userId, dateRangeDto.getFrom().atStartOfDay(), dateRangeDto.getTo().atStartOfDay());
            return appointments.stream()
                    .map(appointmentMapper::toAppointmentDto)
                    .toList();
        }
        if (role == PATIENT && state == FREE) {
            throw new RuntimeException();
        }

        if (role == DOCTOR && state == OCCUPIED) {
            List<Appointment> appointments = appointmentRepository.findByDoctorIdAndDateRange(
                    userId, dateRangeDto.getFrom().atStartOfDay(), dateRangeDto.getTo().atStartOfDay());
            return appointments.stream()
                    .map(appointmentMapper::toAppointmentDto)
                    .toList();
        }
        if (role == DOCTOR && state == FREE) {
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

        LocalDate currentDate = dateRangeDto.getFrom();
        LocalDate endDate = dateRangeDto.getTo();

        Map<DayOfWeek, WorkingTimeDto> workingTimesMap = initializeWorkingTimeMap(doctorWorkingTimes);

        while (currentDate.isBefore(endDate)) {
            WorkingTimeDto workingTimeForDay = workingTimesMap.get(currentDate.getDayOfWeek());
            if (workingTimeForDay != null) {
                LocalTime currentTime = workingTimeForDay.getStartTime();
                LocalTime endTime = workingTimeForDay.getEndTime();
                Integer interval = workingTimeForDay.getTimeIntervalInMinutes();

                while (currentTime.isBefore(endTime)) {
                    if (!isBreak(currentTime, workingTimeForDay)) {
                        freeDates.add(LocalDateTime.of(currentDate, currentTime));
                    }
                    currentTime = currentTime.plusMinutes(interval);
                }
            }
            currentDate = currentDate.plusDays(1);
        }

        List<LocalDateTime> occupiedDates = occupiedAppointments.stream()
                .map(Appointment::getDate)
                .toList();

        return freeDates.stream()
                .filter(date -> !occupiedDates.contains(date))
                .toList();
    }

    private Map<DayOfWeek, WorkingTimeDto> initializeWorkingTimeMap(List<WorkingTimeDto> workingTimeDtoList) {
        return workingTimeDtoList.stream().collect(Collectors.toMap(t -> t.getDay(), t -> t));
    }

    private boolean isBreak(LocalTime currentTime, WorkingTimeDto workingTimeDto) {
        LocalTime breakStartTime = workingTimeDto.getBreakStartTime();
        LocalTime breakEndTime = workingTimeDto.getBreakEndTime();

        if (breakStartTime == null || breakEndTime == null) {
            return false;
        }
        return currentTime.isBefore(breakEndTime) &&
               currentTime.isAfter(breakStartTime.minusMinutes(workingTimeDto.getTimeIntervalInMinutes()));
    }

    public void delete(Role role, UUID doctorId, UUID appointmentId) {
        verifyUserExistByRoleAndId(role, doctorId);
        verifyAppointmentExistById(appointmentId);

        appointmentRepository.deleteById(appointmentId);
    }

    private UserDto verifyUserExistByRoleAndId(Role role, UUID id) {
        UserDto user = userServiceClient.findById(role, id);
        if (user.getRole() != role) {
            throw new UserNotFoundException(String.format(USER_NOT_FOUND_BY_ID_AND_ROLE, id, role));
        }
        return user;
    }

    private void verifyDoctorIsWorking(UserDto doctor) {
        if (!doctor.getIsWorking()) {
            throw new DoctorNotWorkingException(DOCTOR_NOT_WORKING);
        }
    }

    private void verifyAppointmentExistById(UUID appointmentId) {
        if (appointmentRepository.findById(appointmentId).isEmpty()) {
            throw new AppointmentNotFoundException(APPOINTMENT_NOT_FOUND + appointmentId);
        }
    }

    private void verifyAppointmentNotExistByIdAndDate(UUID doctorId, LocalDateTime date) {
        if (appointmentRepository.findByDoctorIdAndDate(doctorId, date).isPresent()) {
            throw new AppointmentAlreadyExistException(APPOINTMENT_ALREADY_EXIST);
        }
    }
}
