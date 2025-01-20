package ru.sberbank.pprb.quote.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.sberbank.pprb.quote.api.dto.ELvlInfoDto;
import ru.sberbank.pprb.quote.api.resource.ELvlResource;
import ru.sberbank.pprb.quote.service.ELvlService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ELvlController implements ELvlResource {

    private final ELvlService eLvlService;

    @Override
    public ResponseEntity<ELvlInfoDto> getELvl(String isin) {
        var eLvl = eLvlService.findByIsin(isin);
        return ResponseEntity.status(HttpStatus.OK).body(eLvl);
    }

    @Override
    public ResponseEntity<List<ELvlInfoDto>> list(int page, int size) {
        var all = eLvlService.findAll(page, size);
        return ResponseEntity.status(HttpStatus.OK).body(all);
    }
}
