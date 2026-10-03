package com.d4igen.visualizar_cep.exception;

import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

@RestControllerAdvice 
public class GlobalExceptionHandler {
    
    @ExceptionHandler(CepException.class)
    public ResponseEntity<String> handle(CepException e) {

        return ResponseEntity.status(e.getStatus()).body(e.getMessage());
    }

    @ExceptionHandler (HttpClientErrorException.class)
    public ResponseEntity<String> handleViaCepIndisponivel(RuntimeException e) {

        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
        .body("Serviço de consulta de CEP indisponível");

    }

    @ExceptionHandler(Exception.class) 
    public ResponseEntity<String> handleGenerico(Exception e) {
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body("Erro interno");

    }


}
