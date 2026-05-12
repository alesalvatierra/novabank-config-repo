package com.novabank.cuenta.service;

import com.novabank.cuenta.model.Cuenta;
import com.novabank.cuenta.model.Movimiento;
import com.novabank.cuenta.repository.CuentaRepository;
import com.novabank.cuenta.repository.MovimientoRepository;
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

    public Mono<Cuenta> crearCuenta(Cuenta cuenta) {
        return webClient.get()
                .uri("http://cliente-service/api/clientes/{id}", cuenta.getClienteId())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                        response -> Mono.error(new IllegalArgumentException("El cliente no existe")))
                .bodyToMono(Object.class)
                .flatMap(cliente -> {
                    if (cuenta.getSaldo() == null) {
                        cuenta.setSaldo(0.0);
                    }
                    if (cuenta.getEstado() == null) {
                        cuenta.setEstado(true);
                    }
                    return cuentaRepository.save(cuenta);
                });
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
