package com.novabank.operacion.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.novabank.operacion.dto.CuentaDTO;
import com.novabank.operacion.dto.ExchangeRateResponse;
import com.novabank.operacion.model.Operacion;
import com.novabank.operacion.repository.OperacionRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Service
public class OperacionService {

    private final OperacionRepository operacionRepository;
    private final WebClient webClient;
    private final Cache<String, Double> rateCache;

    public OperacionService(OperacionRepository operacionRepository, WebClient.Builder webClientBuilder) {
        this.operacionRepository = operacionRepository;
        this.webClient = webClientBuilder.build();
        this.rateCache = Caffeine.newBuilder()
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .build();
    }

    public Mono<Operacion> realizarTransferencia(Long origenId, Long destinoId, Double montoUsd) {

        Mono<CuentaDTO> cuentaMono = webClient.get()
                .uri("http://cuenta-service/api/cuentas/{id}", origenId)
                .retrieve()
                .bodyToMono(CuentaDTO.class);

        Mono<Double> rateMono = obtenerTipoCambio("USD", "EUR");

        return Mono.zip(cuentaMono, rateMono)
                .flatMap(tupla -> {
                    CuentaDTO cuenta = tupla.getT1();
                    Double rate = tupla.getT2();

                    Double montoEur = montoUsd * rate;

                    if (cuenta.getSaldo() == null || cuenta.getSaldo() < montoEur) {
                        return Mono.error(new RuntimeException("Saldo insuficiente en la cuenta origen"));
                    }

                    boolean isCached = rateCache.getIfPresent("USD_EUR") != null
                            && rate.equals(rateCache.getIfPresent("USD_EUR"));

                    String descripcion = isCached
                            ? "Transferencia USD (Tasa Cacheada)"
                            : "Transferencia USD";

                    Mono<Void> retirar = webClient.put()
                            .uri("http://cuenta-service/api/cuentas/{id}/saldo?monto={monto}", origenId, -montoEur)
                            .retrieve()
                            .bodyToMono(Void.class);

                    Mono<Void> ingresar = webClient.put()
                            .uri("http://cuenta-service/api/cuentas/{id}/saldo?monto={monto}", destinoId, montoEur)
                            .retrieve()
                            .bodyToMono(Void.class);

                    Operacion operacion = new Operacion(
                            null,
                            origenId,
                            destinoId,
                            montoEur,
                            descripcion,
                            LocalDateTime.now()
                    );

                    return Mono.when(retirar, ingresar)
                            .then(operacionRepository.save(operacion));
                });
    }

    @CircuitBreaker(name = "exchangeRateService", fallbackMethod = "fallbackTipoCambio")
    @Retry(name = "exchangeRateService")
    public Mono<Double> obtenerTipoCambio(String from, String to) {
        return webClient.get()
                .uri("http://exchange-rate-mock-service/api/exchange/rate?from={from}&to={to}", from, to)
                .retrieve()
                .bodyToMono(ExchangeRateResponse.class)
                .map(response -> {
                    Double rate = response.getRate();
                    rateCache.put(from + "_" + to, rate);
                    return rate;
                });
    }

    public Mono<Double> fallbackTipoCambio(String from, String to, Throwable ex) {
        Double cachedRate = rateCache.getIfPresent(from + "_" + to);
        if (cachedRate != null) {
            return Mono.just(cachedRate);
        }
        return Mono.error(new RuntimeException(
                "Servicio de divisas no disponible y no hay tasa en caché para " + from + " a " + to
        ));
    }
}
