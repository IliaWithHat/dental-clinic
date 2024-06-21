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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;
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

    public AppointmentDto create(CreateAppointmentDto createAppointmentDto, Role role, UUID userId) {
        if (role != DOCTOR) {
            throw new RuntimeException();
        }
        Appointment savedAppointment = appointmentRepository.save(appointmentMapper.toAppointment(createAppointmentDto));
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

    public AppointmentDto update(UpdateAppointmentDto updateAppointmentDto, UUID appointmentId, Role role, UUID userId) {
        return appointmentRepository.findById(appointmentId)
                .map(oldAppointment -> appointmentMapper.updateAppointment(updateAppointmentDto, oldAppointment))
                .map(appointmentRepository::save)
                .map(appointmentMapper::toAppointmentDto)
                .orElseThrow();
    }

    public AppointmentDto findById(UUID appointmentId, Role role, UUID userId) {
        if (role == PATIENT) {
            throw new RuntimeException();
        }
        return appointmentRepository.findById(appointmentId)
                .filter(appointment -> appointment.getDoctorId().equals(userId))
                .map(appointmentMapper::toAppointmentDto)
                .orElseThrow();
    }

    public List<AppointmentDto> find(DateRangeDto dateRangeDto, State state, Role role, UUID userId) {
        boolean ignoreDateRange = dateRangeDto.equals(new DateRangeDto(null, null));

        if (role == PATIENT && state == OCCUPIED) {
            List<Appointment> appointments;
            if (ignoreDateRange) {
                appointments = appointmentRepository.findByPatientId(userId);
            } else {
                appointments = appointmentRepository.findByPatientIdAndDateRange(
                        userId, dateRangeDto.getFrom().atStartOfDay(), dateRangeDto.getTo().atStartOfDay());
            }
            return appointments.stream()
                    .map(appointmentMapper::toAppointmentDto)
                    .toList();
        }
        if (role == PATIENT && state == FREE) {
            throw new RuntimeException();
        }

        if (role == DOCTOR && state == OCCUPIED) {
            List<Appointment> appointments;
            if (ignoreDateRange) {
                appointments = appointmentRepository.findByDoctorId(userId);
            } else {
                appointments = appointmentRepository.findByDoctorIdAndDateRange(
                        userId, dateRangeDto.getFrom().atStartOfDay(), dateRangeDto.getTo().atStartOfDay());
            }
            return appointments.stream()
                    .map(appointmentMapper::toAppointmentDto)
                    .toList();
        }
        if (role == DOCTOR && state == FREE) {
            List<WorkingTimeDto> workingTimeDtos = timeServiceClient.findByDoctorId(userId);

            List<Appointment> occupiedAppointments;
            if (ignoreDateRange) {
                throw new RuntimeException();
            } else {
                occupiedAppointments = appointmentRepository.findByDoctorIdAndDateRange(
                        userId, dateRangeDto.getFrom().atStartOfDay(), dateRangeDto.getTo().atStartOfDay());
            }

            return generateFreeDates(workingTimeDtos, occupiedAppointments, dateRangeDto).stream()
                    .map(date -> AppointmentDto.builder()
                            .date(date)
                            .doctorId(userId)
                            .build())
                    .toList();
        }

        throw new RuntimeException();
    }

    private List<LocalDateTime> generateFreeDates(List<WorkingTimeDto> workingTimeDtos, List<Appointment> occupiedAppointments, DateRangeDto dateRangeDto) {
        List<LocalDateTime> freeDates = new ArrayList<>();

        LocalDate currentDate = dateRangeDto.getFrom();
        LocalDate endDate = dateRangeDto.getTo();

        while (currentDate.isBefore(endDate)) {
            Optional<WorkingTimeDto> workingTimeForDay = getWorkingTimeForDay(workingTimeDtos, currentDate.getDayOfWeek());

            if (workingTimeForDay.isPresent()) {
                WorkingTimeDto workingTimeDto = workingTimeForDay.get();
                LocalTime currentTime = workingTimeDto.getStartTime();
                LocalTime endTime = workingTimeDto.getEndTime();
                Integer interval = workingTimeDto.getTimeIntervalInMinutes();

                while (currentTime.isBefore(endTime)) {
                    if (!isBreak(currentTime, workingTimeDto)) {
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

    private boolean isBreak(LocalTime currentTime, WorkingTimeDto workingTimeDto) {
        LocalTime breakStartTime = workingTimeDto.getBreakStartTime();
        LocalTime breakEndTime = workingTimeDto.getBreakEndTime();

        if (breakStartTime == null || breakEndTime == null) {
            return false;
        }
        return currentTime.isBefore(breakEndTime) &&
               currentTime.isAfter(breakStartTime.minusMinutes(workingTimeDto.getTimeIntervalInMinutes()));
    }

    private Optional<WorkingTimeDto> getWorkingTimeForDay(List<WorkingTimeDto> workingTimeDtos, DayOfWeek dayOfWeek) {
        return workingTimeDtos.stream()
                .filter(workingTimeDto -> workingTimeDto.getDay() == dayOfWeek)
                .findFirst();
    }

    public void delete(UUID appointmentId, Role role, UUID userId) {
        appointmentRepository.deleteById(appointmentId);
    }
}
