package com.novabank.cliente.service;

import com.novabank.cliente.dto.ClienteDTO;
import com.novabank.cliente.exception.ClienteNotFoundException;
import com.novabank.cliente.model.Cliente;
import com.novabank.cliente.repository.ClienteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private ClienteService clienteService;

    @Test
    public void testCrearCliente_Exito() {

        // Preparamos el DTO que viene desde Postman
        ClienteDTO nuevoClienteDTO = new ClienteDTO(null, "Ana", "García", "12345678A", "ana@novabank.com", "600123456");

        // Preparamos la Entidad que simula devolver la base de datos (ya con el ID 1L asignado)
        Cliente clienteGuardado = new Cliente("Ana", "García", "12345678A", "ana@novabank.com", "600123456");
        clienteGuardado.setId(1L);

        // Simulamos el guardado
        when(clienteRepository.save(any(Cliente.class))).thenReturn(clienteGuardado);


        // Llamamos al método, pasándole y recibiendo un DTO
        ClienteDTO resultado = clienteService.crearCliente(nuevoClienteDTO);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId()); // Comprobamos que el ID se ha mapeado bien
        assertEquals("Ana", resultado.getNombre());
        verify(clienteRepository, times(1)).save(any(Cliente.class));
    }

    @Test
    public void testObtenerPorId_NoEncontrado_LanzaExcepcion() {
        //Simulamos que la BD no encuentra el ID 99
        when(clienteRepository.findById(99L)).thenReturn(Optional.empty());


        // Comprobamos que llama al método obtenerPorId y salta la excepción
        assertThrows(ClienteNotFoundException.class, () -> {
            clienteService.obtenerPorId(99L);
        });

        // Verificamos que se llamó a la base de datos
        verify(clienteRepository, times(1)).findById(99L);
    }
}
