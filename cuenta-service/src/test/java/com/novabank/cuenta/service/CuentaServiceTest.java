package com.novabank.cuenta.service;

import com.novabank.cuenta.model.Cuenta;
import com.novabank.cuenta.repository.CuentaRepository;
import com.novabank.cuenta.repository.MovimientoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class CuentaServiceTest {

    @Mock
    private CuentaRepository cuentaRepository;

    @Mock
    private MovimientoRepository movimientoRepository;

    @InjectMocks
    private CuentaService cuentaService;

    @Test
    public void testActualizarSaldoExitoso() {

        Cuenta cuenta = new Cuenta();
        cuenta.setId(1L);
        cuenta.setSaldo(100.0);

        when(cuentaRepository.findById(1L)).thenReturn(Optional.of(cuenta));

        cuentaService.actualizarSaldo(1L, 50.0);

        assertEquals(150.0, cuenta.getSaldo());
        verify(cuentaRepository, times(1)).save(cuenta);
        verify(movimientoRepository, times(1)).save(any());
    }
}
