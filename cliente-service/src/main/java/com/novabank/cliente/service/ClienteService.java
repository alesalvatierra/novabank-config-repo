package com.novabank.cliente.service;

import com.novabank.cliente.dto.ClienteDTO;
import com.novabank.cliente.exception.ClienteNotFoundException;
import com.novabank.cliente.model.Cliente;
import com.novabank.cliente.repository.ClienteRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    //Inyección de dependencias por Spring
    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public List<ClienteDTO> obtenerTodos() {
        return clienteRepository.findAll().stream()
                .map(this::mapearADto)
                .collect(Collectors.toList());
    }

    public ClienteDTO obtenerPorId(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ClienteNotFoundException("No se ha encontrado el cliente con ID: " + id));
        return mapearADto(cliente);
    }

    public ClienteDTO obtenerPorDni(String dni) {
        Cliente cliente = clienteRepository.findByDni(dni)
                .orElseThrow(() -> new ClienteNotFoundException("No se ha encontrado cliente con DNI: " + dni));
        return mapearADto(cliente);
    }

    public ClienteDTO crearCliente(ClienteDTO clienteDTO) {
        Cliente cliente = new Cliente(
                clienteDTO.getNombre(),
                clienteDTO.getApellidos(),
                clienteDTO.getDni(),
                clienteDTO.getEmail(),
                clienteDTO.getTelefono()
        );

        Cliente clienteGuardado = clienteRepository.save(cliente);

        return mapearADto(clienteGuardado);
    }

    //Método auxiliar privado para no repetir código
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
}
