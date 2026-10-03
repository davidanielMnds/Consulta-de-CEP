package com.d4igen.visualizar_cep.controller;

import com.d4igen.visualizar_cep.dto.EnderecoDTO;
import com.d4igen.visualizar_cep.service.CepService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/cep")
public class CepController {
    @Autowired
    private CepService cepService;

    //------------------- GET cep
    @GetMapping("/{cep}")
    public ResponseEntity<EnderecoDTO> buscarCep(@PathVariable String cep) {
        return ResponseEntity.ok(cepService.buscarCEP(cep));
    }


    //------------------- GET historico
    @GetMapping("/historico")
    public List<EnderecoDTO> historico() {
        return cepService.getHistorico();
    }


    //-------------------- GET historico somente CEP
    @GetMapping("/historico/cep")
    public List<String> historicoCep(){
        return cepService.getHistoricoCep();
    }
}
