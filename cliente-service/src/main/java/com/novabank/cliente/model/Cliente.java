package com.novabank.cliente.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("clientes")
public class Cliente {

    @Id
    private Long id;

    private String nombre;
    private String apellidos;
    private String dni;
    private String email;
    private String telefono;

    @Column("fecha_creacion")
    private LocalDateTime fechaCreacion;
}
