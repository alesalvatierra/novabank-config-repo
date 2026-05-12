package com.novabank.operacion.controller;

import com.novabank.operacion.model.Operacion;
import com.novabank.operacion.service.OperacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/operaciones")
@RequiredArgsConstructor
public class OperacionController {

    private final OperacionService operacionService;

    @PostMapping("/transferencia-usd")
    public Mono<Operacion> transferirUsd(@RequestParam Long origenId,
                                         @RequestParam Long destinoId,
                                         @RequestParam Double montoUsd) {
        return operacionService.realizarTransferencia(origenId, destinoId, montoUsd);
    }
}
