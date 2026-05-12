package com.novabank.cuenta.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("cuentas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cuenta {

    @Id
    private Long id;

    @Column("numero_cuenta")
    private String numeroCuenta;

    private Double saldo;

    private Boolean estado;

    @Column("cliente_id")
    private Long clienteId;
}
