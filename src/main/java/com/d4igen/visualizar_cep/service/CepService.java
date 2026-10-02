package com.d4igen.visualizar_cep.service;

import com.d4igen.visualizar_cep.dto.EnderecoDTO;

import exception.CepNaoEncontradoException;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class CepService {
    private List<EnderecoDTO> historico = new ArrayList<>();
    private static final String VIA_CEP_URL = "https://viacep.com.br/ws/{cep}/json/";
    private RestTemplate restTemplate;

    public CepService(RestTemplate restTemplate) {
        this.restTemplate=restTemplate;
    }

  
    public EnderecoDTO buscarCEP(String cep) {
        EnderecoDTO endereco = restTemplate.getForObject(VIA_CEP_URL, EnderecoDTO.class, cep);
        if(endereco == null || endereco.getLocalidade() == null) {
            throw new CepNaoEncontradoException(cep);
        }
        historico.add(endereco);
        return endereco;
    }

    public List<EnderecoDTO> getHistorico() {
        return historico;
    }
    public List<String> getHistoricoCep() {
        List<String> listaCeps = new ArrayList<>();
        for(EnderecoDTO a : historico) {
            listaCeps.add(a.getCep());
        }
        return listaCeps;
    }
}
