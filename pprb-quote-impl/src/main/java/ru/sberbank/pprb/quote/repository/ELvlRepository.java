package ru.sberbank.pprb.quote.repository;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.sberbank.pprb.quote.persistence.ELvl;

import java.util.Optional;
import java.util.UUID;

public interface ELvlRepository extends JpaRepository<ELvl, UUID> {

    Optional<ELvl> findByIsin(String isin);
}