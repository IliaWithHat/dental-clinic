package org.ilia.mailgeneratorservice.kafka;

import lombok.RequiredArgsConstructor;
import org.ilia.mailgeneratorservice.entity.MailDetails;
import org.ilia.mailgeneratorservice.service.MailGeneratorService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaConsumer {

    private final MailGeneratorService mailGeneratorService;

    @KafkaListener(topics = "mail-generator", groupId = "mail-generator-service")
    public void listen(MailDetails mailDetails) {
        mailGeneratorService.generateMail(mailDetails);
    }
}
