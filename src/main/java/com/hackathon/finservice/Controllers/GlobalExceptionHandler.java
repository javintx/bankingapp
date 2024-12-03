package com.hackathon.finservice.Controllers;

import jakarta.persistence.PersistenceException;
import java.util.stream.Collectors;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<String> handleBadRequestExceptions(MethodArgumentNotValidException ex) {
    return ResponseEntity
        .badRequest()
        .body(
            ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining("; "))
        );
  }

  @ExceptionHandler(PersistenceException.class)
  public ResponseEntity<Object> handlePersistenceException(PersistenceException ex) {
    return ResponseEntity
        .badRequest()
        .body(ex.getMessage());
  }

  @ExceptionHandler(DataAccessException.class)
  public ResponseEntity<Object> handleDataAccessException(DataAccessException ex) {
    return ResponseEntity
        .badRequest()
        .body(ex.getMessage());
  }

  @ExceptionHandler(HttpMessageConversionException.class)
  public ResponseEntity<Object> handleHttpMessageConversionException(HttpMessageConversionException ex,
      WebRequest request) {
    return ResponseEntity
        .badRequest()
        .body(ex.getMostSpecificCause().getMessage());
  }
}