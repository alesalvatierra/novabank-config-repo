package com.novabank.cuenta.service;

import com.novabank.cuenta.client.ClienteClient;
import com.novabank.cuenta.model.Cuenta;
import com.novabank.cuenta.model.Movimiento;
import com.novabank.cuenta.repository.CuentaRepository;
import com.novabank.cuenta.repository.MovimientoRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CuentaService {

    private final CuentaRepository cuentaRepository;
    private final ClienteClient clienteClient;
    private final MovimientoRepository movimientoRepository;

    @Transactional
    @CircuitBreaker(name = "clienteServiceCB", fallbackMethod = "fallbackCrearCuenta")
    public Cuenta crearCuenta(Cuenta cuenta) {
        //Validar que el cliente existe llamando al microservicio de clientes
        try {
            clienteClient.obtenerCliente(cuenta.getClienteId());
        } catch (Exception e) {
            throw new RuntimeException("No se puede crear la cuenta: El cliente con ID " + cuenta.getClienteId() + " no existe.");
        }

        //Si el cliente es válido, guardamos la cuenta
        return cuentaRepository.save(cuenta);
    }

    // Método de rescate (Fallback) si el cliente-service falla
    public Cuenta fallbackCrearCuenta(Cuenta cuenta, Throwable t) {
        throw new RuntimeException("Servicio de validación temporalmente inactivo. No se puede verificar al cliente en este momento.");
    }

    @Transactional
    public void actualizarSaldo(Long id, Double monto) {
        Cuenta cuenta = cuentaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cuenta con ID " + id + " no encontrada"));

        Double nuevoSaldo = cuenta.getSaldo() + monto;

        if (nuevoSaldo < 0) {
            throw new RuntimeException("La operación resultaría en un saldo negativo");
        }

        //Actualizamos el saldo de la cuenta
        cuenta.setSaldo(nuevoSaldo);
        cuentaRepository.save(cuenta);

        //Registramos el movimiento en el historial (Auditoría)
        Movimiento movimiento = new Movimiento();
        movimiento.setCuentaId(id);
        movimiento.setMonto(monto);
        movimiento.setSaldoDespues(nuevoSaldo);

        movimientoRepository.save(movimiento);
    }
}
