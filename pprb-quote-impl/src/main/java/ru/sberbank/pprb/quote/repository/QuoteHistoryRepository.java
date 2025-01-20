package ru.sberbank.pprb.quote.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.sberbank.pprb.quote.persistence.QuoteHistory;

import java.util.UUID;

public interface QuoteHistoryRepository extends JpaRepository<QuoteHistory, UUID> {
}