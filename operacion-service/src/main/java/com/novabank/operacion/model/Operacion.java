package com.novabank.operacion.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
public class Operacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long cuentaOrigenId;
    private Long cuentaDestinoId;

    private Double monto;

    private LocalDateTime fechaOperacion;

    @PrePersist
    protected void onCreate() {
        fechaOperacion = LocalDateTime.now();
    }
}
