package ru.sberbank.pprb.quote.api.resource;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.sberbank.pprb.quote.api.dto.ELvlInfoDto;

import java.util.List;


@RequestMapping("/elvl")
@Tag(name = "ELvlResource", description = "API для работы с energy level котировок")
public interface ELvlResource {

    @GetMapping("/{isin}")
    @Operation(summary = "Получение elvl котировок по ISIN")
    ResponseEntity<ELvlInfoDto> getELvl(@PathVariable("isin") String isin);

    @GetMapping(value = "/list", params = {"page", "size"})
    @Operation(summary = "Получение списка elvl")
    ResponseEntity<List<ELvlInfoDto>> list(@RequestParam("page") int page,
                                           @RequestParam("size") int size);

}
