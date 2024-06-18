package org.ilia.mailschedulerservice.configuration;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaConfiguration {

    @Bean
    public NewTopic appointmentTopic() {
        return new NewTopic("mail-sender", 1, (short) 1);
    }
}
