package ru.sberbank.pprb.quote.api.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.sberbank.pprb.quote.api.dto.QuoteDto;


/**
 * Валидация bid с ask
 * @author SagdievIA
 * @since 15.01.2025
 */
public class BidLessThanAskValidator implements ConstraintValidator<BidLessThanAsk, QuoteDto> {

    /**
     * bid должен быть меньше ask
     * @param quote Котировка
     * @return true - если bid < ask
     */
    @Override
    public boolean isValid(QuoteDto quote, ConstraintValidatorContext context) {
        if (quote == null) {
            return false;
        }

        var bid = quote.getBid();
        var ask = quote.getAsk();

        if (bid != null && ask == null) {
            return true;
        }

        if (bid != null) {
            return bid.compareTo(ask) < 0;
        }

        return false;
    }
}
