package com.novabank.exchangerate.controller;

import com.novabank.exchangerate.dto.ExchangeRateResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.Duration;

@RestController
@RequestMapping("/api/exchange")
public class ExchangeRateController {

    @GetMapping("/rate")
    public Mono<ExchangeRateResponse> getExchangeRate(
            @RequestParam(defaultValue = "USD") String from,
            @RequestParam(defaultValue = "EUR") String to) {

        ExchangeRateResponse response = new ExchangeRateResponse(from, to, 0.92);

        return Mono.just(response)
                .delayElement(Duration.ofMillis(500));
    }
}
