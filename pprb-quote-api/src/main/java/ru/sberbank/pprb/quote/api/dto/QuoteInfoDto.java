package ru.sberbank.pprb.quote.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.Date;

@Getter
@Setter
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "QuoteInfoDto", description = "Информация по котировкам")
public class QuoteInfoDto extends QuoteDto {

    @Schema(name = "message", description = "Ответ")
    private String message;

    // ToDo не знаю нужно ли это знать клиенту, в ТЗ об этом молчат, но мне кажется клиенту будет полезно знать
    @Schema(name = "createDate", description = "Дата создания котировки")
    private Date createDate;

}
