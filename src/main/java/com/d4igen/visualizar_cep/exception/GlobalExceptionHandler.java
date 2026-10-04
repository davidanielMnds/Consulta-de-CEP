package com.d4igen.visualizar_cep.exception;

import com.d4igen.visualizar_cep.dto.ErroDTO;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.RestClientException;

@RestControllerAdvice 
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    @ExceptionHandler(CepException.class)
    public ResponseEntity<ErroDTO> handle(CepException e) {

        return ResponseEntity.status(e.getStatus()).body(new ErroDTO(e.getMessage()));
    }

    @ExceptionHandler (RestClientException.class)
    public ResponseEntity<ErroDTO> handleViaCepIndisponivel(RestClientException e) {

        log.error("Falha ao consultar o ViaCEP", e);

        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
        .body(new ErroDTO("Serviço de consulta de CEP indisponível"));

    }

    @ExceptionHandler(Exception.class) 
    public ResponseEntity<ErroDTO> handleGenerico(Exception e) {

        log.error("Erro inesperado", e);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(new ErroDTO("Erro interno"));

    }


}
