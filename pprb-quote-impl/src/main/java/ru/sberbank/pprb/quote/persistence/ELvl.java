package ru.sberbank.pprb.quote.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "E_LVL")
public class ELvl extends LastUpdateDateEntity{

    @Id
    @UuidGenerator
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;

    @Size(max = 12)
    @NotNull
    @Column(name = "ISIN", nullable = false, length = 12)
    private String isin;

    @NotNull
    @Column(name = "ENERGY_LEVEL", nullable = false, precision = 10, scale = 1)
    private BigDecimal energyLevel;

}
