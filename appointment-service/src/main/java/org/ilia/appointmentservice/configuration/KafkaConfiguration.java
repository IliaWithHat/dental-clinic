package org.ilia.appointmentservice.configuration;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaConfiguration {

    @Bean
    public NewTopic appointmentTopic() {
        return new NewTopic("mail-generator", 1, (short) 1);
    }
}
