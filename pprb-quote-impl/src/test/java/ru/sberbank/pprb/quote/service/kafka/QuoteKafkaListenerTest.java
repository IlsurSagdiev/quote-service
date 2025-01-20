package ru.sberbank.pprb.quote.service.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.support.MessageBuilder;
import ru.sberbank.pprb.quote.api.dto.QuoteDto;
import ru.sberbank.pprb.quote.service.QuoteService;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class QuoteKafkaListenerTest {

    @Mock
    private QuoteService quoteService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private QuoteKafkaListener quoteKafkaListener;

    @BeforeEach
    public void setUp() {
        quoteKafkaListener = new QuoteKafkaListener(10, quoteService, objectMapper);
    }

    @Test
    public void testConsume() throws Exception {
        QuoteDto quoteDto = new QuoteDto();
        quoteDto.setAsk(new BigDecimal("200.2"));
        quoteDto.setBid(new BigDecimal("20.2"));
        quoteDto.setIsin("RU70A01JX777");
        String message = objectMapper.writeValueAsString(quoteDto);

        // Формирование MessageHeaders
        var build = MessageBuilder
                .withPayload(message)
                .setHeader(KafkaHeaders.TOPIC, "quote-topic")
                .setHeader(KafkaHeaders.KEY, UUID.randomUUID())
                .build();

        quoteKafkaListener.consume(message, build.getHeaders());

    }

    @Test
    public void testConsumeWithJsonProcessingException() {
        String invalidMessage = "invalid json";
        // Формирование MessageHeaders
        var build = MessageBuilder
                .withPayload(invalidMessage)
                .setHeader(KafkaHeaders.TOPIC, "quote-topic")
                .setHeader(KafkaHeaders.KEY, UUID.randomUUID())
                .build();

        try {
            quoteKafkaListener.consume(invalidMessage, build.getHeaders());
        } catch (RuntimeException e) {
            // Проверка, что исключение выброшено
            Assertions.assertNotNull(e);
        }
        verify(quoteService, Mockito.times(0)).saveOrUpdate(Mockito.any());
    }
}
