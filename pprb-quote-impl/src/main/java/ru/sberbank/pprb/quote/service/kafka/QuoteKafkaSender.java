package ru.sberbank.pprb.quote.service.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import ru.sberbank.pprb.quote.api.dto.QuoteDto;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuoteKafkaSender {
    @Value("${spring.kafka.topic}")
    private String quoteTopic;
    @Getter
    private String lastError = null;

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void sendToQuoteTopic(QuoteDto quoteDto) throws JsonProcessingException {
        log.debug("Отправка котировки ISIN = {} в топик = {}", quoteDto.getIsin(), quoteTopic);
        send(quoteTopic, quoteDto);
    }

    public void send(@NonNull String topic, @NonNull Object body) {
        try {
            var messageStr = body instanceof String ? (String) body : objectMapper.writeValueAsString(body);
            final var quoteMsg = this.createMessage(topic, messageStr);

            // @ToDo добавить ретрай? добавить хэдэры, все это опускаем. при желании это легко сделать)
            var send = kafkaTemplate.send(quoteMsg);
            send.whenComplete((sendResult, exception) -> {
                if (exception == null) {
                    this.lastError = "";
                } else {
                    this.lastError = exception.getMessage();
                    log.error("Ошибка отправки сообщения: topic = {}, errorMsg = {}", topic, lastError, exception);
                    throw new RuntimeException(exception);
                }
            });
        } catch (JsonProcessingException ex) {
            throw new IllegalArgumentException("Ошибка сериализации: " + ex.getMessage(), ex);
        }
    }


    private Message<String> createMessage(String topic, String data) {
        return MessageBuilder
                .withPayload(data)
                .setHeader(KafkaHeaders.TOPIC, topic)
                .build();
    }
}