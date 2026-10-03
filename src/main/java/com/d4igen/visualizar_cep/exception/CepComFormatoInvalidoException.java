package com.d4igen.visualizar_cep.exception;

import org.springframework.http.HttpStatus;

public class CepComFormatoInvalidoException extends CepException{
    public CepComFormatoInvalidoException(String cep) {
        super("cep com formato inválido: " + cep, HttpStatus.BAD_REQUEST);
    }

}
