package com.d4igen.visualizar_cep.service;
import com.d4igen.visualizar_cep.dto.EnderecoDTO;
import com.d4igen.visualizar_cep.service.CepService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CepServiceTest {
    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private CepService cepService;

    @Test
    void deveAdicionarNoHistoricoQuandoCepExistir() {

        EnderecoDTO dto = new EnderecoDTO();
        dto.setCep("00000000");
        dto.setLocalidade("Belo Horizonte");
        
        when(restTemplate.getForObject(anyString(), eq(EnderecoDTO.class), eq("00000000")))
        .thenReturn(dto);

        EnderecoDTO resultado = cepService.buscarCEP("00000000");

        System.out.println("CEP: " + dto.getCep());
        System.out.println("Localidade: " + dto.getLocalidade());
        System.out.println("Historico: " + cepService.getHistoricoCep());

        assertEquals("Belo Horizonte", resultado.getLocalidade());
        assertEquals(1, cepService.getHistorico().size());
    }

    @Test 
    void naoDeveAdicionarNoHistoricoQuandoCepNaoExiste() {
        when(restTemplate.getForObject(anyString(), eq(EnderecoDTO.class), eq("00000000")))
        .thenReturn(new EnderecoDTO());
        cepService.buscarCEP("00000000");
        assertTrue(cepService.getHistorico().isEmpty()); 
    }

}
