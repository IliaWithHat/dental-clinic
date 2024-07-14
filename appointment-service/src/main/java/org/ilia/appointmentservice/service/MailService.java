package org.ilia.appointmentservice.service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.ilia.appointmentservice.entity.Appointment;
import org.ilia.appointmentservice.entity.MailDetails;
import org.ilia.appointmentservice.enums.Subject;
import org.ilia.appointmentservice.feign.response.UserDto;
import org.ilia.appointmentservice.kafka.KafkaProducer;
import org.springframework.stereotype.Service;

import static lombok.AccessLevel.PRIVATE;
import static org.ilia.appointmentservice.enums.Subject.APPOINTMENT_CONFIRMATION;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class MailService {

    KafkaProducer kafkaProducer;

    public void sendAppointmentConfirmationEmail(UserDto doctor, UserDto patient, Appointment appointment) {
        MailDetails mailDetails = buildMailDetails(doctor, patient, appointment, APPOINTMENT_CONFIRMATION);
        kafkaProducer.send(mailDetails);
    }

    private MailDetails buildMailDetails(UserDto doctor, UserDto patient, Appointment appointment, Subject subject) {
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
