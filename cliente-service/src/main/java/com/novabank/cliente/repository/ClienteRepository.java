package com.novabank.cliente.repository;

import com.novabank.cliente.model.Cliente;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface ClienteRepository extends ReactiveCrudRepository<Cliente, Long> {

    Mono<Cliente>findByDni(String dni);
    Mono<Cliente> findByEmail(String email);
}
