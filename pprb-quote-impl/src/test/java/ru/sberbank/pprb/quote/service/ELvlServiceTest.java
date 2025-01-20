package ru.sberbank.pprb.quote.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.sberbank.pprb.quote.api.dto.ELvlInfoDto;
import ru.sberbank.pprb.quote.mapper.ELvlMapper;
import ru.sberbank.pprb.quote.persistence.ELvl;
import ru.sberbank.pprb.quote.repository.ELvlRepository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ELvlServiceTest {

    @InjectMocks
    private ELvlService eLvlService;

    @Mock
    private ELvlRepository repository;

    @Mock
    private ELvlMapper eLvlMapper;

    private String isin;
    private BigDecimal newBid;
    private BigDecimal newAsk;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        isin = "RU000A0JX0J2";
        newBid = BigDecimal.valueOf(100.0);
        newAsk = BigDecimal.valueOf(90.0);
    }

    @Test
    public void testUpdateELvl_WhenEntityExists() {
        ELvl existingELvl = new ELvl();
        existingELvl.setIsin(isin);
        existingELvl.setEnergyLevel(BigDecimal.valueOf(80.0));

        when(repository.findByIsin(isin)).thenReturn(Optional.of(existingELvl));
        when(repository.saveAndFlush(existingELvl)).thenReturn(existingELvl);

        BigDecimal result = eLvlService.updateELvl(isin, newBid, newAsk);

        assertEquals(newBid, result);
        assertEquals(newBid, existingELvl.getEnergyLevel());
        verify(repository).saveAndFlush(existingELvl);
    }

    @Test
    public void testUpdateELvl_WhenEntityDoesNotExist() {
        when(repository.findByIsin(isin)).thenReturn(Optional.empty());

        var newELvl = new ELvl();
        newELvl.setIsin(isin);
        newELvl.setEnergyLevel(newBid);
        newELvl.setCreateDate(new Date());

        when(repository.saveAndFlush(any())).thenReturn(newELvl);

        BigDecimal result = eLvlService.updateELvl(isin, newBid, newAsk);

        assertEquals(newBid, result);
        verify(repository).saveAndFlush(any());
    }

    @Test
    public void testFindByIsin_WhenEntityExists() {
        ELvl existingELvl = new ELvl();
        existingELvl.setIsin(isin);
        existingELvl.setEnergyLevel(BigDecimal.valueOf(100.0));

        ELvlInfoDto dto = new ELvlInfoDto();
        when(repository.findByIsin(isin)).thenReturn(Optional.of(existingELvl));
        when(eLvlMapper.mapToDto(existingELvl)).thenReturn(dto);

        ELvlInfoDto result = eLvlService.findByIsin(isin);

        assertEquals(dto, result);
        verify(eLvlMapper).mapToDto(existingELvl);
    }

    @Test
    public void testFindByIsin_WhenEntityDoesNotExist() {
        when(repository.findByIsin(isin)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> eLvlService.findByIsin(isin));

        assertEquals("Не удалось получить Energy Level по ISIN = " + isin, exception.getMessage());
    }

    @Test
    public void testFindAll() {
        ELvl eLvl1 = new ELvl();
        ELvl eLvl2 = new ELvl();
        Page<ELvl> pageElvl = new PageImpl<>(List.of(eLvl1, eLvl2));

        int page = 0;
        int size = 2;
        PageRequest pageRequest = PageRequest.of(page, size);


        when(repository.findAll(pageRequest)).thenReturn(pageElvl);
        when(eLvlMapper.mapToDto(eLvl1)).thenReturn(new ELvlInfoDto());
        when(eLvlMapper.mapToDto(eLvl2)).thenReturn(new ELvlInfoDto());

        List<ELvlInfoDto> result = eLvlService.findAll(0, 2);

        assertEquals(2, result.size());
        verify(repository).findAll(pageRequest);
        verify(eLvlMapper, times(2)).mapToDto(ArgumentMatchers.any());
    }
}