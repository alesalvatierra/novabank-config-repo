package com.novabank.cuenta.repository;

import com.novabank.cuenta.model.Cuenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CuentaRepository extends ReactiveCrudRepository<Cuenta, Long> {

}
