package com.novabank.operacion.service;

import com.novabank.operacion.client.CuentaClient;
import com.novabank.operacion.dto.CuentaDTO;
import com.novabank.operacion.model.Operacion;
import com.novabank.operacion.repository.OperacionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OperacionService {

    private final OperacionRepository operacionRepository;
    private final CuentaClient cuentaClient;

    @Transactional
    public Operacion realizarTransferencia(Long idOrigen, Long idDestino, Double monto) {

        //Obtener los DTOs desde el cuenta-service
        CuentaDTO cuentaOrigen = cuentaClient.obtenerCuenta(idOrigen);
        CuentaDTO cuentaDestino = cuentaClient.obtenerCuenta(idDestino);

        //Validar reglas de negocio
        if (cuentaOrigen.getSaldo() < monto) {
            throw new RuntimeException("Saldo insuficiente para realizar la transferencia");
        }


        // Restamos al origen y sumamos al destino
        cuentaClient.actualizarSaldo(idOrigen, -monto);
        cuentaClient.actualizarSaldo(idDestino, monto);

        //Guardar el registro inmutable de la operación
        Operacion operacion = new Operacion();
        operacion.setCuentaOrigenId(idOrigen);
        operacion.setCuentaDestinoId(idDestino);
        operacion.setMonto(monto);

        return operacionRepository.save(operacion);
    }
}
