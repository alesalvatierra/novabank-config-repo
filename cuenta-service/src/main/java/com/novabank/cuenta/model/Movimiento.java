package com.novabank.cuenta.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("movimientos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Movimiento {

    @Id
    private Long id;

    @Column("cuenta_id")
    private Long cuentaId;

    private Double monto;

    @Column("saldo_despues")
    private Double saldoDespues;

    @Column("fecha_movimiento")
    private LocalDateTime fechaMovimiento;
}
