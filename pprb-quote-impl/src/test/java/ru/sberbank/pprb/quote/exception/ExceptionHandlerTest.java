package ru.sberbank.pprb.quote.exception;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import ru.sberbank.pprb.quote.controller.ELvlController;
import ru.sberbank.pprb.quote.controller.QuoteController;
import ru.sberbank.pprb.quote.service.ELvlService;
import ru.sberbank.pprb.quote.service.QuoteService;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = {QuoteController.class, ELvlController.class, ExceptionHandler.class})
public class ExceptionHandlerTest {

    @MockBean
    private ELvlService eLvlService;
    @MockBean
    private QuoteService quoteService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testHandleValidationException() throws Exception {
        mockMvc.perform(post("/quote")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(REQUEST))
                .andExpect(status().isBadRequest())
                // Ожидаем, что будет сообщение об ошибке валидации
                .andExpect(content().string(containsString("Bid должен быть меньше Ask")));
    }

    private static final String REQUEST = "{\n" +
            "  \"isin\": \"RU171A1JX111\",\n" +
            "  \"bid\": 11031.9,\n" +
            "  \"ask\": 123.9\n" +
            "}";
}