package com.novabank.cliente.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("clientes")
public class Cliente {

    @Id
    private Long id;

    private String nombre;
    private String apellido;
    private String dni;
    private String email;
    private String telefono;

    public Cliente(String nombre, String dni, String email, String telefono) {
    }
}