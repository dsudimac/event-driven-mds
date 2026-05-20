package com.danijelsudimac.orderapiservice.service;

import com.danijelsudimac.orderapiservice.model.CreateOrderEvent;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.MDC;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

import static com.danijelsudimac.orderapiservice.configuration.KafkaConfiguration.TOPIC;
import static com.danijelsudimac.orderapiservice.controller.OrderController.TRACE_ID_KEY;

@Service
@RequiredArgsConstructor
public class OrderPublisher {

    private final KafkaTemplate<String, CreateOrderEvent> kafkaTemplate;

    public void publish(CreateOrderEvent event) {

        var traceId = MDC.get(TRACE_ID_KEY);
        ProducerRecord<String, CreateOrderEvent> record =
                new ProducerRecord<>(TOPIC, event.getOrderId(), event);

        if (traceId != null) {
            record.headers().add(
                    TRACE_ID_KEY,
                    traceId.getBytes(StandardCharsets.UTF_8)
            );
        }

        kafkaTemplate.send(record);
    }
}
