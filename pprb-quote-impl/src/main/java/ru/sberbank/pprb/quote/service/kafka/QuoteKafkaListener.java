package ru.sberbank.pprb.quote.service.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import ru.sberbank.pprb.quote.api.dto.QuoteDto;
import ru.sberbank.pprb.quote.service.QuoteService;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static ru.sberbank.pprb.quote.config.QuoteKafkaConfig.QUOTE_KAFKA_LISTENER_CONTAINER_FACTORY;

/**
 * Консьюмер для обработки сообщений
 * @author IlsurSagdiev
 * @since 16.01.2025
 */
@Slf4j
@Service
public class QuoteKafkaListener {

    @Value("${spring.kafka.topic:quote-topic}")
    private String quoteTopic;

    private static final String QUOTE_LISTENER_ID = "quoteListener";
    private final ExecutorService executorService;
    private final QuoteService quoteService;
    private final ObjectMapper objectMapper;

    public QuoteKafkaListener(@Value("${spring.kafka.consumer.executor-service-thread-pool-size:10}")
                              Integer threadPoolSize,
                              QuoteService quoteService, ObjectMapper objectMapper) {
        this.executorService = Executors.newFixedThreadPool(threadPoolSize);
        this.quoteService = quoteService;
        this.objectMapper = objectMapper;
    }

    /**
     * Подписка на топик quote-topic.
     * Список топиков берется из таблицы KAFKA_TOPICS
     * @param message - сообщение из Kafka
     * @param headers - headers из Kafka
     */
    @KafkaListener(
            id = QUOTE_LISTENER_ID,
            topics = "${spring.kafka.topic:quote-topic}",
            groupId = "${spring.kafka.consumer.group-id:quote}",
            containerFactory = QUOTE_KAFKA_LISTENER_CONTAINER_FACTORY,
            concurrency = "${spring.kafka.consumer.concurrency:3}"
    )
    public void consume(@Payload String message, @Headers MessageHeaders headers) {
        executorService.submit(() -> this.handleQuote(message, headers));
    }

    private void handleQuote(final String message, final MessageHeaders headers) {

        final var receivedKey = headers.get(KafkaHeaders.RECEIVED_KEY);

        log.debug("Принято сообщение из топика. topicName = {}, receivedKey = {}", quoteTopic, receivedKey);
        try {
            var dto = objectMapper.readValue(message, QuoteDto.class);
            quoteService.saveOrUpdate(dto);
        } catch (JsonProcessingException e) {
            log.error("Произошла ошибка при попытке прочитать сообщение из топика = {}, receivedKey = {}, error = {}",
                    quoteTopic, receivedKey, e.getMessage());
            throw new RuntimeException(e);
        }
        log.debug("Сообщение успешно обработано. topicName = {}, receivedKey = {}", quoteTopic, receivedKey);
    }
}
