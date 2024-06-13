package org.ilia.mailsenderservice.kafka;

import lombok.RequiredArgsConstructor;
import org.ilia.mailsenderservice.entity.EmailDetails;
import org.ilia.mailsenderservice.service.MailService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaConsumer {

    private final MailService mailService;

    @KafkaListener(topics = "appointment-mail", groupId = "appointment-mail-service")
    public void listen(EmailDetails emailDetails) {
        mailService.sendEmail(emailDetails);
    }
}
