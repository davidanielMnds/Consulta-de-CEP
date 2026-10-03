package com.d4igen.visualizar_cep.exception;

import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

@RestControllerAdvice 
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    @ExceptionHandler(CepException.class)
    public ResponseEntity<String> handle(CepException e) {

        return ResponseEntity.status(e.getStatus()).body(e.getMessage());
    }

    @ExceptionHandler (HttpClientErrorException.class)
    public ResponseEntity<String> handleViaCepIndisponivel(RuntimeException e) {

        log.error("Falha ao consultar o ViaCEP", e);

        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
        .body("Serviço de consulta de CEP indisponível");

    }

    @ExceptionHandler(Exception.class) 
    public ResponseEntity<String> handleGenerico(Exception e) {

        log.error("Erro inesperado", e);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body("Erro interno");

    }


}
