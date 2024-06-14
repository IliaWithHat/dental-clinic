package org.ilia.mailsenderservice.kafka;

import lombok.RequiredArgsConstructor;
import org.ilia.mailsenderservice.entity.MailDetails;
import org.ilia.mailsenderservice.service.MailService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaConsumer {

    private final MailService mailService;

    @KafkaListener(topics = "mail-sender", groupId = "mail-sender-service")
    public void listen(MailDetails mailDetails) {
        mailService.sendEmail(mailDetails);
    }
}
