
package com.novabank.cuenta.service;

import com.novabank.cuenta.model.Cuenta;
import com.novabank.cuenta.repository.CuentaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CuentaService {

    private final CuentaRepository cuentaRepository;

    @Transactional
    public void actualizarSaldo(Long id, Double monto) {
        //Buscamos la cuenta en la base de datos
        Cuenta cuenta = cuentaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cuenta con ID " + id + " no encontrada"));

        //Calculamos el nuevo saldo
        Double nuevoSaldo = cuenta.getSaldo() + monto;

        //Validamos que no se quede en números rojos
        if (nuevoSaldo < 0) {
            throw new RuntimeException("La operación resultaría en un saldo negativo");
        }

        //Guardamos el nuevo saldo
        cuenta.setSaldo(nuevoSaldo);
        cuentaRepository.save(cuenta);
    }
}