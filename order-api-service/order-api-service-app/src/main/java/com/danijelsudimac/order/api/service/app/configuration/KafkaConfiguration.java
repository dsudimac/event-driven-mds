package com.danijelsudimac.order.api.service.app.configuration;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfiguration {

    public static final String TOPIC = "inventory.orders.v1";
    public static final String TOPIC_INVALID_MESSAGES = "inventory.orders.invalid.v1-dlt";
    private static final String TOPIC_DLT = TOPIC + "-dlt";
    @Bean
    public NewTopic orderTopic() {
        return TopicBuilder.name(TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic orderDlt() {
        return TopicBuilder.name(TOPIC_DLT)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic invalidMessagesDlt() {
        return TopicBuilder.name(TOPIC_INVALID_MESSAGES)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
