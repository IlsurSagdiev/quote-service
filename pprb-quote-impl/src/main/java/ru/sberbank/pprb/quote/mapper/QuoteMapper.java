package ru.sberbank.pprb.quote.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import ru.sberbank.pprb.quote.api.dto.QuoteDto;
import ru.sberbank.pprb.quote.persistence.Quote;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface QuoteMapper {

    Quote map(QuoteDto dto);

    QuoteDto toDto(Quote quote);
}
