package com.novabank.cuenta.repository;

import com.novabank.cuenta.model.Movimiento;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface MovimientoRepository extends ReactiveCrudRepository<Movimiento, Long> {

    Flux<Movimiento> findByCuentaIdOrderByFechaMovimientoDesc(Long cuentaId);
}
