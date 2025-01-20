package ru.sberbank.pprb.quote.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.sberbank.pprb.quote.api.dto.QuoteDto;
import ru.sberbank.pprb.quote.api.dto.QuoteInfoDto;
import ru.sberbank.pprb.quote.api.resource.QuoteResource;
import ru.sberbank.pprb.quote.service.QuoteService;

import java.util.List;

/**
 * Контроллер обработки котировок
 * @author SagdievIA
 * @since 15.01.2025
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class QuoteController implements QuoteResource {

    private final QuoteService quoteService;


    @Override
    public ResponseEntity<QuoteInfoDto> add(QuoteDto quoteDto) {
        var result = quoteService.add(quoteDto);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @Override
    public ResponseEntity<List<QuoteDto>> list(Integer page, Integer size) {
        var all = quoteService.findAll(page, size);
        return ResponseEntity.status(HttpStatus.OK).body(all);
    }

}
