package ru.sberbank.pprb.quote.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.sberbank.pprb.quote.repository.QuoteHistoryRepository;

import java.math.BigDecimal;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class QuoteHistoryServiceTest {

    @InjectMocks
    private QuoteHistoryService quoteHistoryService;

    @Mock
    private QuoteHistoryRepository repository;

    private String isin;
    private BigDecimal eLvl;
    private Date receiveDate;

    @BeforeEach
    public void setUp() {
        isin = "RU000A0JX0J2";
        eLvl = BigDecimal.valueOf(100.5);
        receiveDate = new Date();
    }

    @Test
    public void testSave_CreatesAndSavesQuoteHistory() {
        // Act
        quoteHistoryService.save(isin, eLvl, receiveDate);

        // Assert
        verify(repository, times(1)).save(argThat(quoteHistory ->
                quoteHistory.getIsin().equals(isin) &&
                        quoteHistory.getELvl().equals(eLvl) &&
                        quoteHistory.getReceiveDate().equals(receiveDate)
        ));
    }
}