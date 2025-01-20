package ru.sberbank.pprb.quote.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "ELvlInfoDto", description = "Информация по Energy Level(elvl)")
public class ELvlInfoDto implements Serializable {
    @Schema(name = "eLvl", description = "Energy Level котировки")
    private BigDecimal eLvl;

    @Schema(name = "isin", description = "ISIN")
    private String isin;

    @Schema(name = "createDate", description = "Дата создания записи")
    private Date createDate;

}
