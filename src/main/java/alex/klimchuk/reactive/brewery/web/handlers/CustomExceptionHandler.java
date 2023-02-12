package alex.klimchuk.reactive.brewery.web.handlers;

import alex.klimchuk.reactive.brewery.web.exceptions.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

/**
 * Copyright Alex Klimchuk (c) 2023.
 */
@ControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Void> handleNotFoundException(NotFoundException exception) {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<List> handleBindException(BindException exception) {
        return new ResponseEntity(exception.getAllErrors(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<List> handleConstraintViolationException(ConstraintViolationException exception) {
        List<String> errors = new ArrayList<>(exception.getConstraintViolations().size());

        exception.getConstraintViolations().forEach(ex -> errors.add(ex.getPropertyPath() + " : " + ex.getMessage()));

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

}
