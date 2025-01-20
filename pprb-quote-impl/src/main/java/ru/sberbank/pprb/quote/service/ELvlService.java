package ru.sberbank.pprb.quote.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.sberbank.pprb.quote.api.dto.ELvlInfoDto;
import ru.sberbank.pprb.quote.mapper.ELvlMapper;
import ru.sberbank.pprb.quote.persistence.ELvl;
import ru.sberbank.pprb.quote.repository.ELvlRepository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Сервис для обработки energy level котировки
 * @author SagdievIA
 * @since 15.01.2025
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ELvlService {

    private final ELvlMapper eLvlMapper;
    private final ELvlRepository repository;

    /**
     * Обновить или создать ELvl
     */
    public BigDecimal updateELvl(String isin, BigDecimal newBid, BigDecimal newAsk) {
        var eLvl = repository.findByIsin(isin).orElse(null);
        var newELvl = BigDecimal.ZERO;
        var eLvlExists = eLvl != null;

        if (eLvlExists) {
            newELvl = eLvl.getEnergyLevel();
        }

        if (newBid.compareTo(newELvl) > 0) {
            newELvl = newBid;
        } else if (newAsk.compareTo(newELvl) < 0) {
            newELvl = newAsk;
        } else if (!eLvlExists) {
            newELvl = newBid;
        } else if (newBid.compareTo(BigDecimal.ZERO) == 0) {
            newELvl = newAsk;
        }

        if (eLvlExists) {
            eLvl.setEnergyLevel(newELvl);
        } else {
            eLvl = new ELvl();
            eLvl.setIsin(isin);
            eLvl.setEnergyLevel(newELvl);
            eLvl.setCreateDate(new Date());
        }
        eLvl.changeLastUpdateDate();

        var savedELvl = repository.saveAndFlush(eLvl);

        return savedELvl.getEnergyLevel();
    }

    public ELvlInfoDto findByIsin(String isin) {
        var eLvl = repository.findByIsin(isin);

        return eLvl.map(eLvlMapper::mapToDto)
                .orElseThrow(() -> new EntityNotFoundException("Не удалось получить Energy Level по ISIN = " + isin));
    }

    public List<ELvlInfoDto> findAll(int page, int size) {
        var all = repository.findAll(PageRequest.of(page, size));
        return all.get()
                .map(eLvlMapper::mapToDto)
                .collect(Collectors.toList());
    }
}
