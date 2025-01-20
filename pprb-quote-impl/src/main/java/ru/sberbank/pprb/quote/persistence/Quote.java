package ru.sberbank.pprb.quote.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@Table(name = "QUOTE")
public class Quote extends LastUpdateDateEntity {

    @Id
    @UuidGenerator
    @SequenceGenerator(name = "quote", sequenceName = "seq_quote_id", allocationSize = 1)
    @Column(name = "ID", nullable = false)
    private UUID id;

    @Column(name = "ISIN", nullable = false)
    private String isin;

    @NotNull
    @Column(name = "BID", nullable = false, precision = 10, scale = 1)
    private BigDecimal bid;

    @NotNull
    @Column(name = "ASK", nullable = false, precision = 10, scale = 1)
    private BigDecimal ask;

}
