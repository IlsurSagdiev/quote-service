package ru.sberbank.pprb.quote;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@EnableKafka
@SpringBootApplication
public class PprbQuoteServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PprbQuoteServiceApplication.class, args);
    }

}