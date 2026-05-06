package com.novabank.operacion.controller;

import com.novabank.operacion.model.Operacion;
import com.novabank.operacion.service.OperacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/operaciones")
@RequiredArgsConstructor
public class OperacionController {

    private final OperacionService operacionService;

    @PostMapping("/transferencia")
    public ResponseEntity<Operacion> realizarTransferencia(
            @RequestParam Long idOrigen,
            @RequestParam Long idDestino,
            @RequestParam Double monto) {

        Operacion nuevaOperacion = operacionService.realizarTransferencia(idOrigen, idDestino, monto);
        return ResponseEntity.ok(nuevaOperacion);
    }
}
