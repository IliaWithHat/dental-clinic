package org.ilia.userservice.service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.ilia.userservice.controller.response.UserDto;
import org.ilia.userservice.entity.MailDetails;
import org.ilia.userservice.enums.Subject;
import org.ilia.userservice.kafka.KafkaProducer;
import org.springframework.stereotype.Service;

import static lombok.AccessLevel.PRIVATE;
import static org.ilia.userservice.enums.Subject.WELCOME;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class MailService {

    KafkaProducer kafkaProducer;

    public void sendWelcomeEmail(UserDto patient) {
        MailDetails mailDetails = buildMailDetails(patient, WELCOME);
        kafkaProducer.send(mailDetails);
    }

    private MailDetails buildMailDetails(UserDto patient, Subject subject) {
        return MailDetails.builder()
                .subject(subject)
                .patientEmail(patient.getEmail())
                .patientFirstName(patient.getFirstName())
                .patientLastName(patient.getLastName())
                .build();
    }
}
