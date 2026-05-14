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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private ClienteService clienteService;

    @Test
    void listarTodos_deberiaRetornarFluxDeClientes() {
        Cliente cliente1 = new Cliente(
                1L,
                "Ana",
                "Lopez",
                "12345678A",
                "ana@test.com",
                "600111222",
                LocalDateTime.now()
        );

        Cliente cliente2 = new Cliente(
                2L,
                "Luis",
                "Perez",
                "87654321B",
                "luis@test.com",
                "600333444",
                LocalDateTime.now()
        );

        when(clienteRepository.findAll()).thenReturn(Flux.just(cliente1, cliente2));

        StepVerifier.create(clienteService.listarTodos())
                .expectNextMatches(dto ->
                        dto.getId().equals(1L) &&
                                dto.getNombre().equals("Ana") &&
                                dto.getApellidos().equals("Lopez") &&
                                dto.getDni().equals("12345678A") &&
                                dto.getEmail().equals("ana@test.com") &&
                                dto.getTelefono().equals("600111222")
                )
                .expectNextMatches(dto ->
                        dto.getId().equals(2L) &&
                                dto.getNombre().equals("Luis") &&
                                dto.getApellidos().equals("Perez") &&
                                dto.getDni().equals("87654321B") &&
                                dto.getEmail().equals("luis@test.com") &&
                                dto.getTelefono().equals("600333444")
                )
                .verifyComplete();
    }

    @Test
    void obtenerPorId_deberiaRetornarClienteSiExiste() {
        Cliente cliente = new Cliente(
                1L,
                "Ana",
                "Lopez",
                "12345678A",
                "ana@test.com",
                "600111222",
                LocalDateTime.now()
        );

        when(clienteRepository.findById(1L)).thenReturn(Mono.just(cliente));

        StepVerifier.create(clienteService.obtenerPorId(1L))
                .expectNextMatches(dto ->
                        dto.getId().equals(1L) &&
                                dto.getNombre().equals("Ana") &&
                                dto.getApellidos().equals("Lopez") &&
                                dto.getDni().equals("12345678A") &&
                                dto.getEmail().equals("ana@test.com") &&
                                dto.getTelefono().equals("600111222")
                )
                .verifyComplete();
    }

    @Test
    void obtenerPorId_deberiaDarErrorSiNoExiste() {
        when(clienteRepository.findById(99L)).thenReturn(Mono.empty());

        StepVerifier.create(clienteService.obtenerPorId(99L))
                .expectErrorMatches(error ->
                        error instanceof ClienteNotFoundException &&
                                error.getMessage().equals("Cliente no encontrado con ID: 99")
                )
                .verify();
    }

    @Test
    void crearCliente_deberiaGuardarYRetornarDto() {
        ClienteDTO dtoEntrada = new ClienteDTO(
                null,
                "Ana",
                "Lopez",
                "12345678A",
                "ana@test.com",
                "600111222"
        );

        Cliente clienteGuardado = new Cliente(
                1L,
                "Ana",
                "Lopez",
                "12345678A",
                "ana@test.com",
                "600111222",
                LocalDateTime.now()
        );

        when(clienteRepository.save(any(Cliente.class))).thenReturn(Mono.just(clienteGuardado));

        StepVerifier.create(clienteService.crearCliente(dtoEntrada))
                .expectNextMatches(dto ->
                        dto.getId().equals(1L) &&
                                dto.getNombre().equals("Ana") &&
                                dto.getApellidos().equals("Lopez") &&
                                dto.getDni().equals("12345678A") &&
                                dto.getEmail().equals("ana@test.com") &&
                                dto.getTelefono().equals("600111222")
                )
                .verifyComplete();
    }
}
