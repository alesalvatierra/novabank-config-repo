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

@Service
public class CuentaService {

    private final CuentaRepository cuentaRepository;
    private final MovimientoRepository movimientoRepository;
    private final WebClient.Builder webClientBuilder;

    private final Sinks.Many<Movimiento> movimientosSink;

    public CuentaService(CuentaRepository cuentaRepository, MovimientoRepository movimientoRepository, WebClient.Builder webClientBuilder) {
        this.cuentaRepository = cuentaRepository;
        this.movimientoRepository = movimientoRepository;
        this.webClientBuilder = webClientBuilder;
        this.movimientosSink = Sinks.many().multicast().onBackpressureBuffer();
    }

    public Mono<Cuenta> crearCuenta(Cuenta cuenta) {
        // Llamada no bloqueante al cliente-service usando WebClient
        return webClientBuilder.build()
                .get()
                .uri("http://cliente-service/api/clientes/" + cuenta.getClienteId())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response -> Mono.error(new IllegalArgumentException("El cliente no existe")))
                .bodyToMono(Object.class)
                .flatMap(cliente -> cuentaRepository.save(cuenta));
    }

    public Mono<Cuenta> obtenerCuenta(Long id) {
        return cuentaRepository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Cuenta no encontrada")));
    }

    public Mono<Movimiento> registrarMovimiento(Movimiento movimiento) {
        // Guardamos el movimiento y luego lo emitimos por el Sink
        return movimientoRepository.save(movimiento)
                .doOnNext(movimientosSink::tryEmitNext);
    }

    public Mono<Cuenta> actualizarSaldo(Long cuentaId, Double monto) {
        return obtenerCuenta(cuentaId)
                .flatMap(cuenta -> {
                    cuenta.setSaldo(cuenta.getSaldo() + monto);
                    return cuentaRepository.save(cuenta);
                });
    }

    //Método para que los clientes se suscriban al flujo de datos en tiempo real
    public Flux<Movimiento> obtenerStreamMovimientos() {
        return movimientosSink.asFlux();
    }
}
