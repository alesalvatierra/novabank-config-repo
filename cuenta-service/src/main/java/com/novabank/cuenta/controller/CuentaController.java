package com.novabank.cuenta.controller;

import com.novabank.cuenta.model.Cuenta;
import com.novabank.cuenta.repository.CuentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cuentas")
public class CuentaController {

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
}
