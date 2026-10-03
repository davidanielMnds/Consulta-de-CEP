package com.d4igen.visualizar_cep.service;
import com.d4igen.visualizar_cep.dto.EnderecoDTO;

import com.d4igen.visualizar_cep.exception.CepComFormatoInvalidoException;
import com.d4igen.visualizar_cep.exception.CepNaoEncontradoException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
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
    void deveLancarExcecaoQuandoCepNaoExiste() {

        when(restTemplate.getForObject(anyString(), eq(EnderecoDTO.class), eq("00000000")))
        .thenReturn(new EnderecoDTO());

        
        assertThrows(CepNaoEncontradoException.class, () -> cepService.buscarCEP("00000000"));
        assertTrue(cepService.getHistorico().isEmpty()); 
    }

    @ParameterizedTest
    @ValueSource(strings = {"123", "abc", "123456789", "1234-678", "asdfghjk", ""})
    void deveLancarExcecaoQuandoCepForInvalido(String cepInvalido) {

        assertThrows(CepComFormatoInvalidoException.class, () -> cepService.buscarCEP("cepInvalido"));
        verify(restTemplate, never()).getForObject(anyString(), eq(EnderecoDTO.class), anyString());
    }

    @Test 
    void devePropagarErroQuandoViaCepFicarForaDoAr() {
        when(restTemplate.getForObject(anyString(), eq(EnderecoDTO.class), eq("00000000")))
        .thenThrow(new ResourceAccessException("fora de ar"));

        assertThrows(ResourceAccessException.class, () -> cepService.buscarCEP("00000000"));
        assertTrue(cepService.getHistorico().isEmpty());
    }

    @Test
    void devePropagarErroQuandoViaCepRetorna5xx() {

        when(restTemplate.getForObject(anyString(), eq(EnderecoDTO.class), eq("00000000")))
        .thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

        assertThrows(HttpServerErrorException.class, () -> cepService.buscarCEP("00000000"));
        assertTrue(cepService.getHistorico().isEmpty());
    }

}
