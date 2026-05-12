package com.novabank.operacion.repository;

import com.novabank.operacion.model.Operacion;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OperacionRepository extends ReactiveCrudRepository<Operacion, Long> {
}
