package com.novabank.operacion.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("operacion")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Operacion {

    @Id
    private Long id;

    @Column("cuenta_origen_id")
    private Long cuentaOrigenId;

    @Column("cuenta_destino_id")
    private Long cuentaDestinoId;

    private Double monto;

    private String descripcion;

    @Column("fecha_operacion")
    private LocalDateTime fechaOperacion;
}
