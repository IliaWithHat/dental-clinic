package org.ilia.appointmentservice.kafka;

import lombok.RequiredArgsConstructor;
import org.ilia.appointmentservice.entity.EmailDetails;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaProducer {

    private final KafkaTemplate<String, EmailDetails> kafkaTemplate;

    public void send(EmailDetails emailDetails) {
        kafkaTemplate.send("appointment-mail", emailDetails);
    }
}
