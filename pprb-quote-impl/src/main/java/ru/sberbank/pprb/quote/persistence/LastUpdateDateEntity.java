package ru.sberbank.pprb.quote.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.Date;


@Getter
@Setter
@SuperBuilder
@MappedSuperclass
public class LastUpdateDateEntity extends CreateDateEntity {

    /**
     * Дата последнего изменения
     */

    @Column(name = "LAST_UPDATE_DATE", nullable = false)
    private Date lastUpdateDate;

    public LastUpdateDateEntity() {
        lastUpdateDate = new Date();
    }

    public Date changeLastUpdateDate() {
        var updateDate = new Date();
        setLastUpdateDate(updateDate);
        return updateDate;
    }
}
