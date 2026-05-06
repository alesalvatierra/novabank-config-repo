package com.novabank.cuenta.controller;

import com.novabank.cuenta.model.Cuenta;
import com.novabank.cuenta.repository.CuentaRepository;
import com.novabank.cuenta.service.CuentaService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cuentas")
@RequiredArgsConstructor
public class CuentaController {

    // Inyectamos el servicio que acabamos de crear
    private final CuentaService cuentaService;

    @Autowired
    private CuentaRepository cuentaRepository;

    @GetMapping
    public List<Cuenta> listarCuentas() {
        return cuentaRepository.findAll();
    }

    @PostMapping
    public Cuenta crearCuenta(@RequestBody Cuenta cuenta) {
        return cuentaRepository.save(cuenta);
    }

    //Añadimos el endpoint para actualizar el saldo
    @PutMapping("/{id}/saldo")
    public ResponseEntity<Void> actualizarSaldo(
            @PathVariable Long id,
            @RequestParam Double monto) {

        cuentaService.actualizarSaldo(id, monto);
        return ResponseEntity.ok().build();
    }
}