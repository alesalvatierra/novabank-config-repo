package com.novabank.operacion.service;

import com.novabank.operacion.client.CuentaClient;
import com.novabank.operacion.dto.MovimientoDTO;
import com.novabank.operacion.model.Operacion;
import com.novabank.operacion.repository.OperacionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OperacionServiceTest {

    @Mock
    private OperacionRepository operacionRepository;

    @Mock
    private CuentaClient cuentaClient;

    @InjectMocks
    private OperacionService operacionService;

    @Test
    public void testRealizarTransferencia_Exito() {

        // Simulamos la cuenta origen con 1000€
        MovimientoDTO cuentaOrigen = new MovimientoDTO();
        cuentaOrigen.setId(1L);
        cuentaOrigen.setSaldo(1000.0);

        // Simulamos la cuenta destino con 500€
        MovimientoDTO cuentaDestino = new MovimientoDTO();
        cuentaDestino.setId(2L);
        cuentaDestino.setSaldo(500.0);

        // Le decimos a Mockito qué debe responder cuando el servicio llame a Feign
        when(cuentaClient.obtenerCuenta(1L)).thenReturn(cuentaOrigen);
        when(cuentaClient.obtenerCuenta(2L)).thenReturn(cuentaDestino);

        // Simulamos el guardado de la operación devolviendo el mismo objeto
        when(operacionRepository.save(any(Operacion.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Intentamos transferir 200€
        Operacion resultado = operacionService.realizarTransferencia(1L, 2L, 200.0);

        // VERIFICACIÓN (Then)
        assertNotNull(resultado);
        assertEquals(200.0, resultado.getMonto());

        // Verificamos que se llamó a Feign para actualizar los saldos exactamente 1 vez para cada cuenta
        verify(cuentaClient, times(1)).actualizarSaldo(1L, -200.0);
        verify(cuentaClient, times(1)).actualizarSaldo(2L, 200.0);
    }

    @Test
    public void testRealizarTransferencia_SaldoInsuficiente() {
        // 1. PREPARACIÓN
        // Simulamos la cuenta origen con solo 50€
        MovimientoDTO cuentaOrigen = new MovimientoDTO();
        cuentaOrigen.setId(1L);
        cuentaOrigen.setSaldo(50.0);

        MovimientoDTO cuentaDestino = new MovimientoDTO();
        cuentaDestino.setId(2L);
        cuentaDestino.setSaldo(500.0);

        when(cuentaClient.obtenerCuenta(1L)).thenReturn(cuentaOrigen);
        when(cuentaClient.obtenerCuenta(2L)).thenReturn(cuentaDestino);

        // Intentamos transferir 200€, lo cual debería lanzar una RuntimeException
        Exception exception = assertThrows(RuntimeException.class, () -> {
            operacionService.realizarTransferencia(1L, 2L, 200.0);
        });

        // Verificamos que el mensaje de error es el correcto
        assertEquals("Saldo insuficiente para realizar la transferencia", exception.getMessage());

        // Verificamos que NUNCA se llegó a llamar a la actualización de saldos
        verify(cuentaClient, never()).actualizarSaldo(anyLong(), anyDouble());
        verify(operacionRepository, never()).save(any());
    }
}
