package com.novabank.operacion.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.novabank.operacion.dto.CuentaDTO;
import com.novabank.operacion.dto.ExchangeRateResponse;
import com.novabank.operacion.model.Operacion;
import com.novabank.operacion.repository.OperacionRepository;
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

        Mono<Double> rateMono = webClient.get()
                .uri("http://exchange-rate-mock-service/api/exchange/rate?from=USD&to=EUR")
                .retrieve()
                .bodyToMono(ExchangeRateResponse.class)
                .map(response -> {
                    Double rate = response.getRate();
                    rateCache.put("USD_EUR", rate);
                    return rate;
                })
                .onErrorResume(error -> {
                    Double cachedRate = rateCache.getIfPresent("USD_EUR");
                    if (cachedRate != null) {
                        return Mono.just(cachedRate);
                    }
                    return Mono.error(new RuntimeException("Servicio de divisas caído y caché vacía"));
                });

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
}
