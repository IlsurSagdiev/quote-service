package ru.sberbank.pprb.quote.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.sberbank.pprb.quote.api.dto.ELvlInfoDto;
import ru.sberbank.pprb.quote.persistence.ELvl;

@Mapper(
        componentModel = "spring")
public interface ELvlMapper {

    @Mapping(target = "eLvl", source = "energyLevel")
    ELvlInfoDto mapToDto(ELvl eLvl);
}
