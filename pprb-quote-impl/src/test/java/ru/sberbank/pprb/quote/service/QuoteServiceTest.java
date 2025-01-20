package ru.sberbank.pprb.quote.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.sberbank.pprb.quote.Constants;
import ru.sberbank.pprb.quote.api.dto.QuoteDto;
import ru.sberbank.pprb.quote.api.dto.QuoteInfoDto;
import ru.sberbank.pprb.quote.mapper.QuoteMapper;
import ru.sberbank.pprb.quote.persistence.Quote;
import ru.sberbank.pprb.quote.repository.QuoteRepository;
import ru.sberbank.pprb.quote.service.kafka.QuoteKafkaSender;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class QuoteServiceTest {

    @Autowired
    private QuoteService quoteService;

    @MockBean
    private QuoteKafkaSender quoteKafkaSender;

    @MockBean
    private QuoteHistoryService quoteHistoryService;

    @MockBean
    private QuoteMapper quoteMapper;

    @MockBean
    private QuoteRepository quoteRepository;

    @MockBean
    private ELvlService eLvlService;

    // Тестовые данные
    private QuoteDto quoteDto;
    private Quote quote;
    private List<Quote> quotes;

    @BeforeEach
    public void setUp() {
        quoteDto = new QuoteDto();
        quoteDto.setIsin("RU000A0JX0J2");
        quoteDto.setBid(BigDecimal.valueOf(100.5));
        quoteDto.setAsk(BigDecimal.valueOf(101.0));

        quote = new Quote();
        quote.setIsin("RU000A0JX0J2");
        quote.setBid(BigDecimal.valueOf(99.0));
        quote.setAsk(BigDecimal.valueOf(100.0));

        quotes = new ArrayList<>();
        Quote quote1 = new Quote();
        quote1.setIsin("RU000A0JX0J2");
        quote1.setBid(BigDecimal.valueOf(100.5));
        quote1.setAsk(BigDecimal.valueOf(101.0));
        quotes.add(quote1);

        Quote quote2 = new Quote();
        quote2.setIsin("RU000A0JX0J3");
        quote2.setBid(BigDecimal.valueOf(200.5));
        quote2.setAsk(BigDecimal.valueOf(201.0));
        quotes.add(quote2);
    }

    @Test
    public void testAdd_SuccessSendToKafka() throws JsonProcessingException {
        // Arrange
        when(quoteMapper.map(quoteDto)).thenReturn(new Quote());
        when(quoteRepository.saveAndFlush(quote)).thenReturn(quote);
        // Act
        QuoteInfoDto result = quoteService.add(quoteDto);

        // Assert
        verify(quoteKafkaSender, times(1)).sendToQuoteTopic(quoteDto);
        assertEquals("RU000A0JX0J2", result.getIsin());
        assertEquals(BigDecimal.valueOf(101.0), result.getAsk());
        assertEquals(BigDecimal.valueOf(100.5), result.getBid());
        assertNotNull(result.getCreateDate());
        assertEquals(Constants.QUOTE_SEND_TO_HANDLING, result.getMessage());
    }


    @Test
    public void testAdd_ErrorSendToKafka() throws JsonProcessingException {
        // Arrange
        doThrow(new RuntimeException("Kafka Error")).when(quoteKafkaSender).sendToQuoteTopic(any(QuoteDto.class));
        when(quoteMapper.map(quoteDto)).thenReturn(quote);
        when(quoteRepository.saveAndFlush(quote)).thenReturn(quote);

        // Act
        QuoteInfoDto result = quoteService.add(quoteDto);

        // Assert
        verify(quoteKafkaSender, times(1)).sendToQuoteTopic(quoteDto);
        verify(quoteRepository, times(1)).saveAndFlush(any(Quote.class));
        assertEquals("RU000A0JX0J2", result.getIsin());
        assertEquals(new BigDecimal("101.0"), result.getAsk());
        assertEquals(new BigDecimal("100.5"), result.getBid());
        assertNotNull(result.getCreateDate());
        assertEquals(Constants.QUOTE_SUCCESS_HANDLE, result.getMessage());
    }

    @Test
    public void testSaveOrUpdate_UpdateExistingQuote() {
        // Arrange
        when(quoteRepository.findByIsin(quoteDto.getIsin())).thenReturn(Optional.of(quote));
        when(quoteMapper.map(quoteDto)).thenReturn(quote);
        when(quoteRepository.saveAndFlush(quote)).thenReturn(quote);

        // Act
        quoteService.saveOrUpdate(quoteDto);

        // Assert
        verify(quoteRepository, times(1)).findByIsin(quoteDto.getIsin());
        verify(quoteRepository, times(1)).saveAndFlush(quote);
        assertEquals(quoteDto.getBid(), quote.getBid());
        assertEquals(quoteDto.getAsk(), quote.getAsk());
    }

    @Test
    public void testSaveOrUpdate_CreateNewQuote() {
        // Arrange
        when(quoteRepository.findByIsin(quoteDto.getIsin())).thenReturn(Optional.empty());
        when(quoteMapper.map(quoteDto)).thenReturn(new Quote());
        when(quoteRepository.saveAndFlush(any())).thenReturn(quote);

        // Act
        quoteService.saveOrUpdate(quoteDto);

        // Assert
        verify(quoteRepository, times(1)).findByIsin(quoteDto.getIsin());
        verify(quoteRepository, times(1)).saveAndFlush(any(Quote.class));
    }

    @Test
    public void testFindAll_ReturnsQuoteDtos() {
        // Arrange
        Page<Quote> quotePage = new PageImpl<>(quotes);
        when(quoteRepository.findAll(PageRequest.of(0, 10))).thenReturn(quotePage);
        when(quoteMapper.toDto(any(Quote.class))).thenAnswer(invocation -> {
            Quote quote = invocation.getArgument(0);
            QuoteDto dto = new QuoteDto();
            dto.setIsin(quote.getIsin());
            dto.setBid(quote.getBid());
            dto.setAsk(quote.getAsk());
            return dto;
        });

        // Act
        List<QuoteDto> result = quoteService.findAll(0, 10);

        // Assert
        assertEquals(2, result.size());
        assertEquals("RU000A0JX0J2", result.get(0).getIsin());
        assertEquals(BigDecimal.valueOf(100.5), result.get(0).getBid());
        assertEquals(BigDecimal.valueOf(101.0), result.get(0).getAsk());

        assertEquals("RU000A0JX0J3", result.get(1).getIsin());
        assertEquals(BigDecimal.valueOf(200.5), result.get(1).getBid());
        assertEquals(BigDecimal.valueOf(201.0), result.get(1).getAsk());

        verify(quoteRepository, times(1)).findAll(PageRequest.of(0, 10));
        verify(quoteMapper, times(2)).toDto(any(Quote.class));
    }

    @Test
    public void testFindAll_NoQuotes() {
        // Arrange
        Page<Quote> quotePage = new PageImpl<>(new ArrayList<>());
        when(quoteRepository.findAll(PageRequest.of(0, 10))).thenReturn(quotePage);

        // Act
        List<QuoteDto> result = quoteService.findAll(0, 10);

        // Assert
        assertTrue(result.isEmpty());

        verify(quoteRepository, times(1)).findAll(PageRequest.of(0, 10));
        verify(quoteMapper, never()).toDto(any(Quote.class));
    }
}