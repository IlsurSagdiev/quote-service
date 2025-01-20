package ru.sberbank.pprb.quote.api.resource;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.sberbank.pprb.quote.api.dto.QuoteDto;
import ru.sberbank.pprb.quote.api.dto.QuoteInfoDto;

import java.util.List;

@RequestMapping("/quote")
@Tag(name = "QuoteResource", description = "API для работы с котировками")
public interface QuoteResource {

    @PostMapping
    @Operation(summary = "Создание новой котировки")
    ResponseEntity<QuoteInfoDto> add(@Valid
                                     @RequestBody
                                     QuoteDto quoteDto);


    @GetMapping(value = "/list", params = {"page", "size"})
    @Operation(summary = "Получение списка котировок")
    ResponseEntity<List<QuoteDto>> list(@RequestParam("page") @NotNull Integer page,
                                        @RequestParam("size") @NotNull Integer size);

}
