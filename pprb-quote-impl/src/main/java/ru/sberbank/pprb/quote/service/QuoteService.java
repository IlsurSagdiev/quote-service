package ru.sberbank.pprb.quote.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sberbank.pprb.quote.Constants;
import ru.sberbank.pprb.quote.api.dto.QuoteDto;
import ru.sberbank.pprb.quote.api.dto.QuoteInfoDto;
import ru.sberbank.pprb.quote.mapper.QuoteMapper;
import ru.sberbank.pprb.quote.persistence.Quote;
import ru.sberbank.pprb.quote.repository.QuoteRepository;
import ru.sberbank.pprb.quote.service.kafka.QuoteKafkaSender;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static ru.sberbank.pprb.quote.Constants.QUOTE_SEND_TO_HANDLING;
import static ru.sberbank.pprb.quote.Constants.QUOTE_SUCCESS_HANDLE;

/**
 * Сервис для обработки котировок
 * @author SagdievIA
 * @since 15.01.2025
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class QuoteService {

    private final QuoteHistoryService quoteHistoryService;
    private final QuoteKafkaSender quoteKafkaSender;
    private final QuoteMapper quoteMapper;
    private final QuoteRepository quoteRepository;
    private final ELvlService eLvlService;

    @Transactional
    public QuoteInfoDto add(QuoteDto quote) {
        String message;
        try {
            log.debug("Отправка котировки в kafka. isin = {}", quote.getIsin());
            quoteKafkaSender.sendToQuoteTopic(quote);
            message = QUOTE_SEND_TO_HANDLING;
        } catch (RuntimeException | JsonProcessingException e) {
            log.error("Не удалось отправить котировку в kafa. isin = {}", quote.getIsin());
            log.debug("Котировка будет обработана синхронно. isin = {}", quote.getIsin());
            this.saveOrUpdate(quote);
            message = QUOTE_SUCCESS_HANDLE;
        }

        return QuoteInfoDto.builder()
                .isin(quote.getIsin())
                .ask(quote.getAsk())
                .bid(quote.getBid())
                .createDate(new Date())
                .message(message)
                .build();
    }

    /**
     * Добавление котировки
     * @param quoteDto котировка
     */
    @Transactional
    public void saveOrUpdate(QuoteDto quoteDto) {
        var quoteOptional = quoteRepository.findByIsin(quoteDto.getIsin());
        var toSaveQuote = quoteOptional.map(quote -> this.update(quoteDto, quote))
                .orElseGet(() -> this.create(quoteDto));

        var eLvl = eLvlService.updateELvl(toSaveQuote.getIsin(), toSaveQuote.getBid(), toSaveQuote.getAsk());
        toSaveQuote.changeLastUpdateDate();

        var quote = quoteRepository.saveAndFlush(toSaveQuote);
        quoteHistoryService.save(quote.getIsin(), eLvl, quote.getCreateDate());
    }

    /**
     * Получение списка котировок
     * @param page - страница
     * @param size - размер страницы
     * @return список котировок
     */
    public List<QuoteDto> findAll(int page, int size) {
        var all = quoteRepository.findAll(PageRequest.of(page, size));
        return all.get()
                .map(quoteMapper::toDto)
                .collect(Collectors.toList());
    }

    private Quote update(QuoteDto quoteDto, Quote quote) {
        quote.setBid(quoteDto.getBid());
        quote.setAsk(quoteDto.getAsk());

        return quote;
    }

    private Quote create(QuoteDto quoteDto) {
        var toSaveQuote = quoteMapper.map(quoteDto);
        toSaveQuote.setCreateDate(new Date());
        return toSaveQuote;
    }


}
