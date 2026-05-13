package com.novabank.cliente.controller;

import com.novabank.cliente.dto.ClienteDTO;
import com.novabank.cliente.service.ClienteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;

    @GetMapping
    public Flux<ClienteDTO> listar() {
        return clienteService.listarTodos();
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<ClienteDTO>> obtener(@PathVariable Long id) {
        return clienteService.obtenerPorId(id)
                .map(ResponseEntity::ok);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ClienteDTO> crear(@RequestBody ClienteDTO dto) {
        return clienteService.crearCliente(dto);
    }
}
