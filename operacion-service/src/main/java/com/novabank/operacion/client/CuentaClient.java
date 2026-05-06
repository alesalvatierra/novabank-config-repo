package com.novabank.operacion.client;

import com.novabank.operacion.dto.CuentaDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "cuenta-service")
public interface CuentaClient {

    @GetMapping("/api/cuentas/{id}")
    CuentaDTO obtenerCuenta(@PathVariable("id") Long id);

    @PutMapping("/api/cuentas/{id}/saldo")
    void actualizarSaldo(@PathVariable("id") Long id, @RequestParam("monto") Double monto);
}
