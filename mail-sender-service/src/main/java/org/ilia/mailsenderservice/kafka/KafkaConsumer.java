package org.ilia.mailsenderservice.kafka;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaConsumer {

    @KafkaListener(topics = "appointment-mail", groupId = "appointment-mail-service")
    public void listen(ConsumerRecord<String, String> record) {
        System.out.println(record.value());
    }
}
