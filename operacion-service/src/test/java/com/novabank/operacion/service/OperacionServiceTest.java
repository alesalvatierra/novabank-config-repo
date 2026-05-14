package com.novabank.operacion.service;

import com.novabank.operacion.repository.OperacionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;

class OperacionServiceTest {

    @Mock
    private OperacionRepository operacionRepository;

    @Mock
    private WebClient.Builder webClientBuilder;

    @Mock
    private WebClient webClient;

    private OperacionService operacionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(webClientBuilder.build()).thenReturn(webClient);
        operacionService = new OperacionService(operacionRepository, webClientBuilder);
    }

    @Test
    void fallbackTipoCambio_deberiaRetornarValorDesdeCache() {
        operacionService.guardarTipoCambioEnCache("USD", "EUR", 0.92);

        StepVerifier.create(
                        operacionService.fallbackTipoCambio("USD", "EUR", new RuntimeException("Servicio caído"))
                )
                .expectNext(0.92)
                .verifyComplete();
    }

    @Test
    void fallbackTipoCambio_deberiaRetornarErrorSiNoHayCache() {
        StepVerifier.create(
                        operacionService.fallbackTipoCambio("USD", "EUR", new RuntimeException("Servicio caído"))
                )
                .expectErrorMatches(error ->
                        error instanceof RuntimeException &&
                                error.getMessage().equals("Servicio de divisas no disponible y no hay tasa en caché para USD a EUR")
                )
                .verify();
    }
}
