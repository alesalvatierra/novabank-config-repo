package com.novabank.cliente.service;

import com.novabank.cliente.dto.ClienteDTO;
import com.novabank.cliente.exception.ClienteNotFoundException;
import com.novabank.cliente.model.Cliente;
import com.novabank.cliente.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
                .map(this::mapearADto)
                // Si el repositorio devuelve vacío, lanzamos la excepción reactivamente
                .switchIfEmpty(Mono.error(new ClienteNotFoundException("Cliente no encontrado con ID: " + id)));
    }

    public Mono<ClienteDTO> crearCliente(ClienteDTO dto) {
        Cliente entidad = mapearAEntidad(dto);
        return clienteRepository.save(entidad)
                .map(this::mapearADto);
    }

    // Mapeadores
    private ClienteDTO mapearADto(Cliente c) {
        return new ClienteDTO(c.getId(), c.getNombre(), c.getDni(), c.getEmail(), c.getTelefono());
    }

    private Cliente mapearAEntidad(ClienteDTO dto) {
        return new Cliente(dto.getNombre(), dto.getDni(), dto.getEmail(), dto.getTelefono());
    }
}
