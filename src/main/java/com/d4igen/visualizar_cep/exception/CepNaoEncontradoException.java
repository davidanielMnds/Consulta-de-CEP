package com.d4igen.visualizar_cep.exception;

import org.springframework.http.HttpStatus;

public class CepNaoEncontradoException extends CepException{
    public CepNaoEncontradoException(String cep) {
        super("CEP não encontrado: " + cep, HttpStatus.NOT_FOUND);
    }
}
