package com.d4igen.visualizar_cep.controller;

import com.d4igen.visualizar_cep.dto.EnderecoDTO;
import com.d4igen.visualizar_cep.service.CepService;
import com.d4igen.visualizar_cep.service.RateLimitService;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Duration;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CepController.class)
public class CepControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CepService cepService;

    @MockitoBean
    private RateLimitService rateLimitService;

    @BeforeEach
    void liberarRateLimit() {
        Bucket balde = Bucket.builder()
                .addLimit(Bandwidth.builder()
                        .capacity(1000)
                        .refillIntervally(1000, Duration.ofMinutes(1))
                        .build())
                .build();
        when(rateLimitService.getBalde(anyString())).thenReturn(balde);
    }

    @Test
    void deveRetornar200ComEnderecoQuandoCepExistir() throws Exception {
        EnderecoDTO dto = new EnderecoDTO();
        dto.setCep("00000000");
        dto.setLogradouro("Rua Horas Complementares");
        dto.setBairro("Nube");
        dto.setLocalidade("Belo Horizonte");
        dto.setUf("BH");

        when(cepService.buscarCEP("00000000")).thenReturn(dto);

        mockMvc.perform(get("/cep/00000000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cep").value("00000000"))
                .andExpect(jsonPath("$.localidade").value("Belo Horizonte"));
    }
}
