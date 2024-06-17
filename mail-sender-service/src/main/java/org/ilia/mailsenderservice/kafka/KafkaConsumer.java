package org.ilia.mailsenderservice.kafka;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.ilia.mailsenderservice.entity.MailDetails;
import org.ilia.mailsenderservice.service.MailService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import static lombok.AccessLevel.PRIVATE;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class KafkaConsumer {

    MailService mailService;

    @KafkaListener(topics = "mail-sender", groupId = "mail-sender-service")
    public void listen(MailDetails mailDetails) {
        mailService.sendEmailToUser(mailDetails);
    }
}
