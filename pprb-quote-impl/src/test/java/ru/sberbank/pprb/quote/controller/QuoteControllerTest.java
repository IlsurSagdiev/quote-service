package ru.sberbank.pprb.quote.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.sberbank.pprb.quote.api.dto.QuoteDto;
import ru.sberbank.pprb.quote.api.dto.QuoteInfoDto;
import ru.sberbank.pprb.quote.service.QuoteService;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class QuoteControllerTest {

    @Mock
    private QuoteService quoteService;

    @InjectMocks
    private QuoteController quoteController;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(quoteController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    public void testAdd() throws Exception {
        QuoteDto quoteDto = new QuoteDto();
        quoteDto.setAsk(new BigDecimal("200.2"));
        quoteDto.setBid(new BigDecimal("20.2"));
        quoteDto.setIsin("RU70A01JX777");

        QuoteInfoDto quoteInfoDto = new QuoteInfoDto();
        when(quoteService.add(any(QuoteDto.class))).thenReturn(quoteInfoDto);

        mockMvc.perform(post("/quote")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(quoteDto)))
                .andExpect(status().isOk());

        verify(quoteService).add(any(QuoteDto.class));
    }

    @Test
    public void testList() throws Exception {
        QuoteDto quoteDto = new QuoteDto();
        quoteDto.setAsk(new BigDecimal("200.2"));
        quoteDto.setBid(new BigDecimal("20.2"));
        quoteDto.setIsin("RU70A01JX777");

        List<QuoteDto> quotes = Collections.singletonList(quoteDto);
        when(quoteService.findAll(0, 10)).thenReturn(quotes);

        mockMvc.perform(get("/quote/list?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].ask").value("200.2"))
                .andExpect(jsonPath("$[0].bid").value("20.2"))
                .andExpect(jsonPath("$[0].isin").value("RU70A01JX777"));

        verify(quoteService).findAll(0, 10);
    }
}