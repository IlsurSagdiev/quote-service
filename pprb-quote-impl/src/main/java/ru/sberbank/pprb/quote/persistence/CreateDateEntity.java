package ru.sberbank.pprb.quote.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.lang.NonNull;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@SuperBuilder
@MappedSuperclass
public class CreateDateEntity implements Serializable {

    @Column(name = "CREATE_DATE", nullable = false)
    private Date createDate;

    public CreateDateEntity() {
        this.createDate = new Date();
    }
}