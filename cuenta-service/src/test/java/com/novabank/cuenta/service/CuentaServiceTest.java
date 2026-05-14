package com.novabank.cuenta.service;

import com.novabank.cuenta.dto.CuentaDTO;
import com.novabank.cuenta.model.Cuenta;
import com.novabank.cuenta.model.Movimiento;
import com.novabank.cuenta.repository.CuentaRepository;
import com.novabank.cuenta.repository.MovimientoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CuentaServiceTest {

    @Mock
    private CuentaRepository cuentaRepository;

    @Mock
    private MovimientoRepository movimientoRepository;

    @Mock
    private WebClient.Builder webClientBuilder;

    @Mock
    private WebClient webClient;

    private CuentaService cuentaService;

    @BeforeEach
    void setUp() {
        when(webClientBuilder.build()).thenReturn(webClient);
        cuentaService = new CuentaService(cuentaRepository, movimientoRepository, webClientBuilder);
    }

    @Test
    void crearCuenta_deberiaGuardarYRetornarDto() {
        CuentaDTO dto = new CuentaDTO(null, "ES123", 1000.0, true, 1L);

        Cuenta cuentaGuardada = new Cuenta();
        cuentaGuardada.setId(1L);
        cuentaGuardada.setNumeroCuenta("ES123");
        cuentaGuardada.setSaldo(1000.0);
        cuentaGuardada.setEstado(true);
        cuentaGuardada.setClienteId(1L);

        when(cuentaRepository.save(any(Cuenta.class))).thenReturn(Mono.just(cuentaGuardada));

        StepVerifier.create(cuentaService.crearCuenta(dto))
                .expectNextMatches(cuentaDto ->
                        cuentaDto.getId().equals(1L) &&
                                cuentaDto.getNumeroCuenta().equals("ES123") &&
                                cuentaDto.getSaldo().equals(1000.0) &&
                                cuentaDto.getEstado().equals(true) &&
                                cuentaDto.getClienteId().equals(1L)
                )
                .verifyComplete();
    }

    @Test
    void obtenerCuenta_deberiaRetornarCuentaSiExiste() {
        Cuenta cuenta = new Cuenta();
        cuenta.setId(1L);
        cuenta.setNumeroCuenta("ES123");
        cuenta.setSaldo(500.0);
        cuenta.setEstado(true);
        cuenta.setClienteId(1L);

        when(cuentaRepository.findById(1L)).thenReturn(Mono.just(cuenta));

        StepVerifier.create(cuentaService.obtenerCuenta(1L))
                .expectNextMatches(c ->
                        c.getId().equals(1L) &&
                                c.getNumeroCuenta().equals("ES123")
                )
                .verifyComplete();
    }

    @Test
    void obtenerCuenta_deberiaDarErrorSiNoExiste() {
        when(cuentaRepository.findById(99L)).thenReturn(Mono.empty());

        StepVerifier.create(cuentaService.obtenerCuenta(99L))
                .expectErrorMatches(error ->
                        error instanceof RuntimeException &&
                                error.getMessage().equals("Cuenta no encontrada")
                )
                .verify();
    }

    @Test
    void registrarMovimiento_deberiaGuardarMovimientoYAsignarFechaSiEsNull() {
        Movimiento movimiento = new Movimiento();
        movimiento.setId(null);
        movimiento.setCuentaId(1L);
        movimiento.setMonto(100.0);
        movimiento.setSaldoDespues(1100.0);
        movimiento.setFechaMovimiento(null);

        Movimiento movimientoGuardado = new Movimiento(
                1L,
                1L,
                100.0,
                1100.0,
                LocalDateTime.now()
        );

        when(movimientoRepository.save(any(Movimiento.class))).thenReturn(Mono.just(movimientoGuardado));

        StepVerifier.create(cuentaService.registrarMovimiento(movimiento))
                .expectNextMatches(m ->
                        m.getId().equals(1L) &&
                                m.getCuentaId().equals(1L) &&
                                m.getFechaMovimiento() != null
                )
                .verifyComplete();
    }

    @Test
    void actualizarSaldo_deberiaActualizarSaldoYGuardarMovimiento() {
        Cuenta cuenta = new Cuenta();
        cuenta.setId(1L);
        cuenta.setNumeroCuenta("ES123");
        cuenta.setSaldo(1000.0);
        cuenta.setEstado(true);
        cuenta.setClienteId(1L);

        Cuenta cuentaActualizada = new Cuenta();
        cuentaActualizada.setId(1L);
        cuentaActualizada.setNumeroCuenta("ES123");
        cuentaActualizada.setSaldo(1200.0);
        cuentaActualizada.setEstado(true);
        cuentaActualizada.setClienteId(1L);

        Movimiento movimientoGuardado = new Movimiento(
                1L,
                1L,
                200.0,
                1200.0,
                LocalDateTime.now()
        );

        when(cuentaRepository.findById(1L)).thenReturn(Mono.just(cuenta));
        when(cuentaRepository.save(any(Cuenta.class))).thenReturn(Mono.just(cuentaActualizada));
        when(movimientoRepository.save(any(Movimiento.class))).thenReturn(Mono.just(movimientoGuardado));

        StepVerifier.create(cuentaService.actualizarSaldo(1L, 200.0))
                .expectNextMatches(c ->
                        c.getId().equals(1L) &&
                                c.getSaldo().equals(1200.0)
                )
                .verifyComplete();
    }

    @Test
    void actualizarSaldo_deberiaDarErrorSiCuentaInactiva() {
        Cuenta cuenta = new Cuenta();
        cuenta.setId(1L);
        cuenta.setNumeroCuenta("ES123");
        cuenta.setSaldo(1000.0);
        cuenta.setEstado(false);
        cuenta.setClienteId(1L);

        when(cuentaRepository.findById(1L)).thenReturn(Mono.just(cuenta));

        StepVerifier.create(cuentaService.actualizarSaldo(1L, 100.0))
                .expectErrorMatches(error ->
                        error instanceof RuntimeException &&
                                error.getMessage().equals("La cuenta está inactiva")
                )
                .verify();
    }

    @Test
    void actualizarSaldo_deberiaDarErrorSiSaldoInsuficiente() {
        Cuenta cuenta = new Cuenta();
        cuenta.setId(1L);
        cuenta.setNumeroCuenta("ES123");
        cuenta.setSaldo(100.0);
        cuenta.setEstado(true);
        cuenta.setClienteId(1L);

        when(cuentaRepository.findById(1L)).thenReturn(Mono.just(cuenta));

        StepVerifier.create(cuentaService.actualizarSaldo(1L, -200.0))
                .expectErrorMatches(error ->
                        error instanceof RuntimeException &&
                                error.getMessage().equals("Saldo insuficiente")
                )
                .verify();
    }
}
