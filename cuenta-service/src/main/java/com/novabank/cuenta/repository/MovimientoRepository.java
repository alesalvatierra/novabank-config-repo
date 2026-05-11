package com.novabank.cuenta.repository;

import com.novabank.cuenta.model.Movimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovimientoRepository extends ReactiveCrudRepository<Movimiento, Long> {

    List<Movimiento> findByCuentaIdOrderByFechaMovimientoDesc(Long cuentaId);
}