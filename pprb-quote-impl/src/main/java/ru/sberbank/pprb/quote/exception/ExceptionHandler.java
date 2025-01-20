package ru.sberbank.pprb.quote.exception;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Глобальный обработчик ошибок
 * @author SagdievIA
 * @since 15.01.2025
 */
@RestControllerAdvice
public class ExceptionHandler {

    /**
     * Обработка ошибок валидации
     * @param ex ошибка валидации
     * @return ответ клиенту с ошибками в теле
     */
    @ResponseBody
    @org.springframework.web.bind.annotation.ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        var errors = new StringBuilder();
        ex.getBindingResult().getAllErrors()
                .forEach(error -> errors.append(error.getDefaultMessage()).append("\n"));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors.toString());
    }

    @ResponseBody
    @org.springframework.web.bind.annotation.ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleAccessDeniedException(final EntityNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
    }
}