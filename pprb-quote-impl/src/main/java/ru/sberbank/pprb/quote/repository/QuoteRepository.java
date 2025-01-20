package ru.sberbank.pprb.quote.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.sberbank.pprb.quote.persistence.Quote;

import java.util.Optional;

public interface QuoteRepository extends JpaRepository<Quote, Long> {
    Optional<Quote> findByIsin(String isin);
}
