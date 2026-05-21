package com.danijelsudimac.inventory.processing.service.configuration;

import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.SerializationException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.listener.ListenerExecutionFailedException;
import org.springframework.kafka.support.serializer.DeserializationException;
import org.springframework.util.backoff.FixedBackOff;

import java.util.Optional;
import java.util.Set;

@Configuration
@Slf4j
public class KafkaConfiguration {

    public static final String TOPIC = "inventory.orders.v1";
    public static final String DLT_SUFFIX = "-dlt";
    public static final String TOPIC_DLT = TOPIC + DLT_SUFFIX;
    public static final String TOPIC_INVALID_MESSAGES = "inventory.orders.invalid.v1-dlt";
    private static final String ERROR_LOG_MESSAGE = "Error processing record with key {}: {} with message {}. Sending to {} topic";
    private static final Set<Class<? extends Throwable>> INVALID_MESSAGE_EXCEPTIONS = Set.of(
            DeserializationException.class,
            SerializationException.class,
            IllegalArgumentException.class
    ); //poison pill exceptions

    @Bean
    public DefaultErrorHandler errorHandler(KafkaTemplate<Object, Object> template) {
        var recoverer =
                new DeadLetterPublishingRecoverer(template,
                        (record, ex) -> {
                            Throwable rootException = Optional.ofNullable(ex.getCause()).orElse(ex);
                            var topic = resolveTopic(rootException);
                            log.warn(ERROR_LOG_MESSAGE, record.key(), rootException.getClass(), rootException.getMessage(), topic);
                            return new TopicPartition(topic, record.partition());
                        });
        var errorHandler =
                new DefaultErrorHandler(recoverer, new FixedBackOff(1000L, 3));
        errorHandler.setCommitRecovered(true);
        errorHandler.addNotRetryableExceptions(
                IllegalArgumentException.class,
                DeserializationException.class,
                ValidationException.class
        );

        return errorHandler;
    }

    private String resolveTopic(Throwable ex) {
        return INVALID_MESSAGE_EXCEPTIONS.stream()
                .anyMatch(clazz -> clazz.isInstance(ex))
                ? TOPIC_INVALID_MESSAGES
                : TOPIC_DLT;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory(
            ConsumerFactory<String, String> consumerFactory,
            DefaultErrorHandler errorHandler) {
        ConcurrentKafkaListenerContainerFactory<String, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(consumerFactory);
        factory.setCommonErrorHandler(errorHandler);
        factory.setConcurrency(3);
        factory.setBatchListener(false);
        factory.getContainerProperties().setMissingTopicsFatal(false);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);

        return factory;
    }
}