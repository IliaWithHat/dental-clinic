package org.ilia.appointmentservice.kafka;

import lombok.RequiredArgsConstructor;
import org.ilia.appointmentservice.entity.MailDetails;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaProducer {

    private final KafkaTemplate<String, MailDetails> kafkaTemplate;

    public void send(MailDetails mailDetails) {
        kafkaTemplate.send("mail-generator", mailDetails);
    }
}
