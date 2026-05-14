package com.novabank.cliente.service;

import com.novabank.cliente.dto.ClienteDTO;
import com.novabank.cliente.exception.ClienteNotFoundException;
import com.novabank.cliente.model.Cliente;
import com.novabank.cliente.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public Flux<ClienteDTO> listarTodos() {
        return clienteRepository.findAll()
                .map(this::mapearADto);
    }

    public Mono<ClienteDTO> obtenerPorId(Long id) {
        return clienteRepository.findById(id)
                .switchIfEmpty(Mono.error(new ClienteNotFoundException("Cliente no encontrado con ID: " + id)))
                .map(this::mapearADto);
    }

    public Mono<ClienteDTO> crearCliente(ClienteDTO dto) {
        Cliente entidad = mapearAEntidad(dto);
        return clienteRepository.save(entidad)
                .map(this::mapearADto);
    }

    private ClienteDTO mapearADto(Cliente cliente) {
        return new ClienteDTO(
                cliente.getId(),
                cliente.getNombre(),
                cliente.getApellidos(),
                cliente.getDni(),
                cliente.getEmail(),
                cliente.getTelefono()
        );
    }

    private Cliente mapearAEntidad(ClienteDTO dto) {
        return new Cliente(
                null,
                dto.getNombre(),
                dto.getApellidos(),
                dto.getDni(),
                dto.getEmail(),
                dto.getTelefono(),
                LocalDateTime.now()
        );
    }
}
