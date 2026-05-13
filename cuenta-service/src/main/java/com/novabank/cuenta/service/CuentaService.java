package com.novabank.cuenta.service;

import com.novabank.cuenta.dto.CuentaDTO;
import com.novabank.cuenta.model.Cuenta;
import com.novabank.cuenta.model.Movimiento;
import com.novabank.cuenta.repository.CuentaRepository;
import com.novabank.cuenta.repository.MovimientoRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.time.LocalDateTime;

@Service
public class CuentaService {

    private final CuentaRepository cuentaRepository;
    private final MovimientoRepository movimientoRepository;
    private final WebClient webClient;
    private final Sinks.Many<Movimiento> movimientosSink;

    public CuentaService(CuentaRepository cuentaRepository,
                         MovimientoRepository movimientoRepository,
                         WebClient.Builder webClientBuilder) {
        this.cuentaRepository = cuentaRepository;
        this.movimientoRepository = movimientoRepository;
        this.webClient = webClientBuilder.build();
        this.movimientosSink = Sinks.many().multicast().onBackpressureBuffer();
    }

    public Mono<CuentaDTO> crearCuenta(CuentaDTO dto) {
        Cuenta cuenta = new Cuenta();
        cuenta.setNumeroCuenta(dto.getNumeroCuenta());
        cuenta.setSaldo(dto.getSaldo());
        cuenta.setEstado(dto.getEstado());
        cuenta.setClienteId(dto.getClienteId());

        return cuentaRepository.save(cuenta)
                .map(this::mapearADto);
    }

    private CuentaDTO mapearADto(Cuenta cuenta) {
        return new CuentaDTO(
                cuenta.getId(),
                cuenta.getNumeroCuenta(),
                cuenta.getSaldo(),
                cuenta.getEstado(),
                cuenta.getClienteId()
        );
    }

    public Mono<Cuenta> obtenerCuenta(Long id) {
        return cuentaRepository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Cuenta no encontrada")));
    }

    public Mono<Movimiento> registrarMovimiento(Movimiento movimiento) {
        if (movimiento.getFechaMovimiento() == null) {
            movimiento.setFechaMovimiento(LocalDateTime.now());
        }

        return movimientoRepository.save(movimiento)
                .doOnNext(movimientosSink::tryEmitNext);
    }

    public Mono<Cuenta> actualizarSaldo(Long cuentaId, Double monto) {
        return obtenerCuenta(cuentaId)
                .flatMap(cuenta -> {
                    if (Boolean.FALSE.equals(cuenta.getEstado())) {
                        return Mono.error(new RuntimeException("La cuenta está inactiva"));
                    }

                    double saldoActual = cuenta.getSaldo() != null ? cuenta.getSaldo() : 0.0;
                    double nuevoSaldo = saldoActual + monto;

                    if (nuevoSaldo < 0) {
                        return Mono.error(new RuntimeException("Saldo insuficiente"));
                    }

                    cuenta.setSaldo(nuevoSaldo);

                    Movimiento movimiento = new Movimiento(
                            null,
                            cuentaId,
                            monto,
                            nuevoSaldo,
                            LocalDateTime.now()
                    );

                    return cuentaRepository.save(cuenta)
                            .flatMap(cuentaActualizada ->
                                    movimientoRepository.save(movimiento)
                                            .doOnNext(movimientosSink::tryEmitNext)
                                            .thenReturn(cuentaActualizada)
                            );
                });
    }

    public Flux<Movimiento> obtenerStreamMovimientos() {
        return movimientosSink.asFlux();
    }
}
