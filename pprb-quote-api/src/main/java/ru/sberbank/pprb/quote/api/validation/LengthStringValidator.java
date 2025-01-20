package ru.sberbank.pprb.quote.api.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.util.ObjectUtils;


/**
 * Валидация размера строки
 * @author SagdievIA
 * @since 15.01.2025
 */
public class LengthStringValidator implements ConstraintValidator<Length, String> {

    private int value;

    @Override
    public void initialize(Length constraintAnnotation) {
        value = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(String checkValue, ConstraintValidatorContext context) {
        return ObjectUtils.isEmpty(checkValue) || (checkValue.length() == value);
    }

}
