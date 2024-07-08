package org.ilia.mailschedulerservice.service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.ilia.mailschedulerservice.entity.DateRange;
import org.ilia.mailschedulerservice.entity.MailDetails;
import org.ilia.mailschedulerservice.enums.Subject;
import org.ilia.mailschedulerservice.feign.AppointmentServiceClient;
import org.ilia.mailschedulerservice.feign.UserServiceClient;
import org.ilia.mailschedulerservice.feign.response.AppointmentDto;
import org.ilia.mailschedulerservice.feign.response.UserDto;
import org.ilia.mailschedulerservice.kafka.KafkaProducer;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;
import static org.ilia.mailschedulerservice.enums.Role.DOCTOR;
import static org.ilia.mailschedulerservice.enums.Role.PATIENT;
import static org.ilia.mailschedulerservice.enums.Subject.APPOINTMENT_REMINDER;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class SchedulerService {

    TokenService tokenService;
    UserServiceClient userServiceClient;
    AppointmentServiceClient appointmentServiceClient;
    KafkaProducer kafkaProducer;

    @Scheduled(cron = "${cron.send-appointment-reminder-email}")
    public void sendAppointmentReminderEmail() {
        tokenService.initializeToken();
        DateRange dateRange = initializeDateRange();

        List<UserDto> doctors = userServiceClient.findByRole(DOCTOR);
        for (UserDto doctor : doctors) {
            List<AppointmentDto> appointmentForThisDoctor = appointmentServiceClient.find(
                    DOCTOR, doctor.getId(), dateRange.getFrom(), dateRange.getTo());

            for (AppointmentDto appointment : appointmentForThisDoctor) {
                UserDto patient = userServiceClient.findById(PATIENT, appointment.getPatientId());
                MailDetails mailDetails = buildMailDetails(doctor, patient, appointment, APPOINTMENT_REMINDER);
                kafkaProducer.send(mailDetails);
            }
        }
    }

    private DateRange initializeDateRange() {
        LocalDate today = LocalDate.now();
        return new DateRange(today, today.plusDays(1));
    }

    private MailDetails buildMailDetails(UserDto doctor, UserDto patient, AppointmentDto appointment, Subject subject) {
        return MailDetails.builder()
                .subject(subject)
                .patientEmail(patient.getEmail())
                .patientFirstName(patient.getFirstName())
                .patientLastName(patient.getLastName())
                .doctorFirstName(doctor.getFirstName())
                .doctorLastName(doctor.getLastName())
                .appointmentDate(appointment.getDate())
                .build();
    }
}
