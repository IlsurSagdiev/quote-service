package ru.sberbank.pprb.quote.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.sberbank.pprb.quote.api.dto.ELvlInfoDto;
import ru.sberbank.pprb.quote.service.ELvlService;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ELvlControllerTest {

    @Mock
    private ELvlService eLvlService;

    @InjectMocks
    private ELvlController eLvlController;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(eLvlController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    public void testGetELvl() throws Exception {
        String testIsin = "RU1234567890";
        ELvlInfoDto eLvlInfoDto = new ELvlInfoDto();
        eLvlInfoDto.setIsin(testIsin);
        eLvlInfoDto.setELvl(BigDecimal.valueOf(100.5));

        when(eLvlService.findByIsin(testIsin)).thenReturn(eLvlInfoDto);

        mockMvc.perform(get("/elvl/" + testIsin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isin").value(testIsin))
                .andExpect(jsonPath("$.elvl").value(100.5));

        verify(eLvlService).findByIsin(testIsin);
    }

    @Test
    public void testList() throws Exception {
        ELvlInfoDto eLvlInfoDto = new ELvlInfoDto();
        eLvlInfoDto.setIsin("RU1234567890");
        eLvlInfoDto.setELvl(BigDecimal.valueOf((100.5)));
        eLvlInfoDto.setCreateDate(new Date());

        List<ELvlInfoDto> eLvlList = Collections.singletonList(eLvlInfoDto);
        when(eLvlService.findAll(0, 10)).thenReturn(eLvlList);

        mockMvc.perform(get("/elvl/list?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].isin").value("RU1234567890"))
                .andExpect(jsonPath("$[0].elvl").value(100.5));

        verify(eLvlService).findAll(0, 10);
    }
}