package com.danijelsudimac.inventory_processing_service.service;

import com.danijelsudimac.inventory_processing_service.mapper.EventMapper;
import com.danijelsudimac.inventory_processing_service.repository.OrderStore;
import com.danijelsudimac.order.api.service.model.CreateOrderEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.MDC;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import org.springframework.kafka.annotation.BackOff;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventConsumer {

    private static final String TOPIC = "inventory.orders.v1";
    public static final String TRACE_ID_KEY = "traceId";
    private static final String ORDER_RESERVED_MESSAGE = "Order {} reserved";
    private static final String ORDER_REJECTED_MESSAGE = "Order {} rejected";
    private static final String DLT_SUFFIX = "-dlt";
    private static final String DLT_TOPIC = TOPIC + DLT_SUFFIX;
    private static final String DLT_MESSAGE = "Order with id {} received at DTL";

    private final OrderStore orderStore;
    private final EventMapper eventMapper;

    @RetryableTopic(
            backOff = @BackOff(delay = 1000, multiplier = 2.0),
            dltStrategy = DltStrategy.FAIL_ON_ERROR
    )
    @KafkaListener(topics = TOPIC)
    public void consume(ConsumerRecord<String, CreateOrderEvent> consumerRecord, Acknowledgment acknowledgment) {
        handleTraceId(consumerRecord, this::processOrder);
        acknowledgment.acknowledge();
    }

    private void processOrder(ConsumerRecord<String, CreateOrderEvent> record) {
        var order = eventMapper.toOrder(record.value());
        if (orderStore.reserve(order)) {
            log.info(ORDER_RESERVED_MESSAGE, order.orderId());
        } else {
            log.warn(ORDER_REJECTED_MESSAGE, order.orderId());
        }
    }

    @KafkaListener(topics = DLT_TOPIC)
    public void handleDlt(ConsumerRecord<String, CreateOrderEvent> consumerRecord) {
        handleTraceId(consumerRecord, record -> log.error(DLT_MESSAGE, record.value().orderId()));
    }

    private void handleTraceId(ConsumerRecord<String, CreateOrderEvent> record,
                               Consumer<ConsumerRecord<String, CreateOrderEvent>> processingLogic) {
        String traceId = new String(
                record.headers().lastHeader(TRACE_ID_KEY).value(),
                StandardCharsets.UTF_8
        );
        try {
            MDC.put(TRACE_ID_KEY, traceId);
            processingLogic.accept(record);
        } finally {
            MDC.remove(TRACE_ID_KEY);
        }
    }
}
