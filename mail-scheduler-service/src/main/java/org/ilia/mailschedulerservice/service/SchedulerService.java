package org.ilia.mailschedulerservice.service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.ilia.mailschedulerservice.entity.MailDetails;
import org.ilia.mailschedulerservice.feign.AppointmentServiceClient;
import org.ilia.mailschedulerservice.feign.UserServiceClient;
import org.ilia.mailschedulerservice.feign.request.DateRange;
import org.ilia.mailschedulerservice.feign.response.FindAppointmentResponse;
import org.ilia.mailschedulerservice.feign.response.User;
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

    @NonFinal
    DateRange dateRange;

    @Scheduled(cron = "${cron.send-appointment-reminder-email}")
    public void sendAppointmentReminderEmail() {
        tokenService.initializeToken();
        updateDateRange();

        List<User> doctors = userServiceClient.findByRole(DOCTOR);

        for (User doctor : doctors) {
            List<FindAppointmentResponse> appointmentForThisDoctor = appointmentServiceClient.find(
                    dateRange.getFrom(), dateRange.getTo(), DOCTOR, doctor.getId());

            for (FindAppointmentResponse appointment : appointmentForThisDoctor) {
                User patient = userServiceClient.findById(PATIENT, appointment.getPatientId());
                sendEmailToPatientWithAppointmentReminder(doctor, patient, appointment);
            }
        }
    }

    private void updateDateRange() {
        LocalDate today = LocalDate.now();
        if (dateRange == null || dateRange.getFrom().isBefore(today)) {
            dateRange = DateRange.builder()
                    .from(today)
                    .to(today.plusDays(1))
                    .build();
        }
    }

    private void sendEmailToPatientWithAppointmentReminder(User doctor, User patient, FindAppointmentResponse appointment) {
        MailDetails mailDetails = MailDetails.builder()
                .subject(APPOINTMENT_REMINDER)
                .patientEmail(patient.getEmail())
                .patientFirstName(patient.getFirstName())
                .patientLastName(patient.getLastName())
                .doctorFirstName(doctor.getFirstName())
                .doctorLastName(doctor.getLastName())
                .appointmentDate(appointment.getDate())
                .build();

        kafkaProducer.send(mailDetails);
    }
}
