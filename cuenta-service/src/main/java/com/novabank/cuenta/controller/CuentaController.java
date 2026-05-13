package com.novabank.cuenta.controller;

import com.novabank.cuenta.dto.CuentaDTO;
import com.novabank.cuenta.model.Cuenta;
import com.novabank.cuenta.model.Movimiento;
import com.novabank.cuenta.service.CuentaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/cuentas")
@RequiredArgsConstructor
public class CuentaController {

    private final CuentaService cuentaService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<CuentaDTO> crear(@Valid @RequestBody CuentaDTO dto) {
        return cuentaService.crearCuenta(dto);
    }

    @GetMapping("/{id}")
    public Mono<Cuenta> obtener(@PathVariable Long id) {
        return cuentaService.obtenerCuenta(id);
    }

    @PutMapping("/{id}/saldo")
    public Mono<Cuenta> actualizarSaldo(@PathVariable Long id, @RequestParam Double monto) {
        return cuentaService.actualizarSaldo(id, monto);
    }

    @PostMapping("/movimientos")
    public Mono<Movimiento> registrarMovimiento(@RequestBody Movimiento movimiento) {
        return cuentaService.registrarMovimiento(movimiento);
    }

    @GetMapping(value = "/movimientos/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Movimiento> verMovimientosEnDirecto() {
        return cuentaService.obtenerStreamMovimientos();
    }
}
