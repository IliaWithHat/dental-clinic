package org.ilia.appointmentservice.service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.ilia.appointmentservice.controller.request.CreateAppointmentRequest;
import org.ilia.appointmentservice.controller.request.DateRange;
import org.ilia.appointmentservice.controller.request.UpdateAppointmentRequest;
import org.ilia.appointmentservice.controller.response.FindAppointmentResponse;
import org.ilia.appointmentservice.entity.Appointment;
import org.ilia.appointmentservice.entity.WorkingTime;
import org.ilia.appointmentservice.enums.Role;
import org.ilia.appointmentservice.enums.State;
import org.ilia.appointmentservice.feign.TimeServiceClient;
import org.ilia.appointmentservice.kafka.KafkaProducer;
import org.ilia.appointmentservice.mapper.AppointmentMapper;
import org.ilia.appointmentservice.repository.AppointmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;
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
    KafkaProducer kafkaProducer;

    public Appointment create(CreateAppointmentRequest createAppointmentRequest, Role role, UUID userId) {
        //TODO write normal code.
//        kafkaProducer.send(null);
        return appointmentRepository.save(appointmentMapper.toAppointment(createAppointmentRequest));
    }

    public Appointment update(UpdateAppointmentRequest updateAppointmentRequest, UUID appointmentId, Role role, UUID userId) {
        return appointmentRepository.findById(appointmentId)
                .map(oldAppointment -> appointmentMapper.updateAppointment(updateAppointmentRequest, oldAppointment))
                .map(appointmentRepository::save)
                .orElseThrow();
    }

    public List<FindAppointmentResponse> find(DateRange dateRange, State state, Role role, UUID userId) {
        if (dateRange.equals(new DateRange())) {
            initializeDateRange(dateRange);
        }

        if (role == PATIENT && state == OCCUPIED) {
            return appointmentRepository
                    .findByPatientIdAndDateRange(userId, dateRange.getFrom().atStartOfDay(), dateRange.getTo().atStartOfDay())
                    .stream()
                    .map(appointmentMapper::toFindAppointmentResponse)
                    .toList();
        }
        if (role == PATIENT && state == FREE) {
            throw new RuntimeException();
        }

        if (role == DOCTOR && state == OCCUPIED) {
            return appointmentRepository
                    .findByDoctorIdAndDateRange(userId, dateRange.getFrom().atStartOfDay(), dateRange.getTo().atStartOfDay())
                    .stream()
                    .map(appointmentMapper::toFindAppointmentResponse)
                    .toList();
        }
        if (role == DOCTOR && state == FREE) {
            List<WorkingTime> workingTimes = timeServiceClient.findByDoctorId(userId);
            List<Appointment> occupiedAppointments = appointmentRepository
                    .findByDoctorIdAndDateRange(userId, dateRange.getFrom().atStartOfDay(), dateRange.getTo().atStartOfDay());

            return generateFreeDates(workingTimes, occupiedAppointments, dateRange).stream()
                    .map(date -> FindAppointmentResponse.builder()
                            .date(date)
                            .doctorId(userId)
                            .build())
                    .toList();
        }

        throw new RuntimeException();
    }

    public void delete(UUID appointmentId, Role role, UUID userId) {
        appointmentRepository.deleteById(appointmentId);
    }

    private void initializeDateRange(DateRange dateRange) {
        LocalDate now = LocalDate.now();
        int year = now.getYear();
        Month month = now.getMonth();
        dateRange.setFrom(LocalDate.of(year, month, 1));
        dateRange.setTo(LocalDate.of(year, month.plus(1), 1));
    }

    private List<LocalDateTime> generateFreeDates(List<WorkingTime> workingTimes, List<Appointment> occupiedAppointments, DateRange dateRange) {
        List<LocalDateTime> freeDates = new ArrayList<>();

        LocalDate currentDate = dateRange.getFrom();
        LocalDate endDate = dateRange.getTo();

        while (currentDate.isBefore(endDate)) {
            Optional<WorkingTime> workingTimeForDay = getWorkingTimeForDay(workingTimes, currentDate.getDayOfWeek());

            if (workingTimeForDay.isPresent()) {
                WorkingTime workingTime = workingTimeForDay.get();
                LocalTime currentTime = workingTime.getStartTime();
                LocalTime endTime = workingTime.getEndTime();
                Integer interval = workingTime.getTimeIntervalInMinutes();

                while (currentTime.isBefore(endTime)) {
                    if (!isBreak(currentTime, workingTime)) {
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

    private boolean isBreak(LocalTime currentTime, WorkingTime workingTime) {
        LocalTime breakStartTime = workingTime.getBreakStartTime();
        LocalTime breakEndTime = workingTime.getBreakEndTime();

        if (breakStartTime == null || breakEndTime == null) {
            return false;
        }
        return currentTime.isBefore(breakEndTime) &&
               currentTime.isAfter(breakStartTime.minusMinutes(workingTime.getTimeIntervalInMinutes()));
    }

    private Optional<WorkingTime> getWorkingTimeForDay(List<WorkingTime> workingTimes, DayOfWeek dayOfWeek) {
        return workingTimes.stream()
                .filter(workingTime -> workingTime.getDay() == dayOfWeek)
                .findFirst();
    }
}
