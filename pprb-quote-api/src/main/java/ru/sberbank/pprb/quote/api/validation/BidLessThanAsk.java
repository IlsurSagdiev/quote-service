package ru.sberbank.pprb.quote.api.validation;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Валидация поля Bid с полем Ask
 * @author SagdievIA
 */
@Constraint(validatedBy = BidLessThanAskValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface BidLessThanAsk {
    String message() default "Bid должен быть меньше Ask";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
