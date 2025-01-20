package ru.sberbank.pprb.quote.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.sberbank.pprb.quote.persistence.QuoteHistory;
import ru.sberbank.pprb.quote.repository.QuoteHistoryRepository;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Сервис для обработки истории котировок
 * @author SagdievIA
 * @since 15.01.2025
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class QuoteHistoryService {

    private final QuoteHistoryRepository repository;

    public void save(String isin, BigDecimal eLvl, Date receiveDate) {
        var quoteHistory = QuoteHistory.builder()
                .eLvl(eLvl)
                .isin(isin)
                .receiveDate(receiveDate)
                .createDate(new Date())
                .build();

        repository.save(quoteHistory);
    }
}
