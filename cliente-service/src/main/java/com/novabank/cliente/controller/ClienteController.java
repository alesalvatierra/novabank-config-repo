package com.novabank.cliente.controller;

import com.novabank.cliente.dto.ClienteDTO;
import com.novabank.cliente.service.ClienteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    //Inyectamos el servicio que acabamos de crear
    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    //GET /api/clientes
    @GetMapping
    public ResponseEntity<List<ClienteDTO>> listarClientes() {
        return ResponseEntity.ok(clienteService.obtenerTodos());
    }

    //GET /api/clientes/{id}
    @GetMapping("/{id}")
    public ResponseEntity<ClienteDTO> obtenerClientePorId(@PathVariable Long id) {
        return ResponseEntity.ok(clienteService.obtenerPorId(id));
    }

    //GET /api/clientes/dni/{dni}
    @GetMapping("/dni/{dni}")
    public ResponseEntity<ClienteDTO> obtenerClientePorDni(@PathVariable String dni) {
        return ResponseEntity.ok(clienteService.obtenerPorDni(dni));
    }

    //POST /api/clientes
    @PostMapping
    public ResponseEntity<ClienteDTO> crearCliente(@RequestBody ClienteDTO clienteDTO) {
        ClienteDTO nuevoCliente = clienteService.crearCliente(clienteDTO);
        return new ResponseEntity<>(nuevoCliente, HttpStatus.CREATED);
    }
}
