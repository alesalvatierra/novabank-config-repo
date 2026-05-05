package com.novabank.cuenta.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "cuentas")
@Data
public class Cuenta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String numeroCuenta;
    private Double saldo;
    private Long clienteId;
}
