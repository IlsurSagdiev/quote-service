package ru.sberbank.pprb.quote.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@Entity
@SuperBuilder
@Table(name = "QUOTE_HISTORY")
@NoArgsConstructor
@AllArgsConstructor
public class QuoteHistory extends CreateDateEntity {
    @Id
    @UuidGenerator
    @Column(name = "ID", nullable = false)
    private UUID id;

    @Size(max = 12)
    @NotNull
    @Column(name = "ISIN", nullable = false, length = 12)
    private String isin;

    @NotNull
    @Column(name = "RECEIVE_DATE", nullable = false)
    private Date receiveDate;

    @NotNull
    @Column(name = "E_LVL", nullable = false, precision = 10, scale = 1)
    private BigDecimal eLvl;

}