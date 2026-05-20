package com.danijelsudimac.orderapiservice.configuration;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfiguration {

    public static final String TOPIC = "inventory.orders.v1";
    private static final String DLT_SUFFIX = "-dlt";
    @Bean
    public NewTopic orderTopic() {
        return TopicBuilder.name(TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic orderDlt() {
        return TopicBuilder.name(TOPIC + DLT_SUFFIX)
                .partitions(1)
                .replicas(1)
                .build();
    }
}
