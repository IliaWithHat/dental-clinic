package org.ilia.mailschedulerservice.kafka;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.ilia.mailschedulerservice.entity.MailDetails;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import static lombok.AccessLevel.PRIVATE;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class KafkaProducer {

    KafkaTemplate<String, MailDetails> kafkaTemplate;

    public void send(MailDetails mailDetails) {
        kafkaTemplate.send("mail-sender", mailDetails);
    }
}
