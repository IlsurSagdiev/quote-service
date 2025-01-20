package ru.sberbank.pprb.quote.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import ru.sberbank.pprb.quote.api.validation.Length;
import ru.sberbank.pprb.quote.api.validation.BidLessThanAsk;

import java.io.Serializable;
import java.math.BigDecimal;


@Getter
@Setter
@ToString
@SuperBuilder
@BidLessThanAsk
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "QuoteDto", description = "Котировка")
public class QuoteDto implements Serializable {

    @Length(12)
    @Schema(name = "isin", description = "Код ISIN")
    private String isin;

    @Schema(name = "bid", description = "")
    private BigDecimal bid;

    @Schema(name = "ask", description = "")
    private BigDecimal ask;
}
