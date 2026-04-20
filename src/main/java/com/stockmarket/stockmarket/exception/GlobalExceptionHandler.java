package com.stockmarket.stockmarket.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(StockNotFoundException.class)
    public ResponseEntity<String> handleNotFound(StockNotFoundException ex) {
        return ResponseEntity.status(404).body(ex.getMessage());
    }

    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<String> handleBadRequest(InsufficientStockException ex) {
        return ResponseEntity.status(400).body(ex.getMessage());
    }
}