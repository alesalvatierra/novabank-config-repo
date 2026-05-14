package com.novabank.operacion.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CuentaDTO {

    private Long id;
    private String numeroCuenta;
    private Double saldo;
    private Boolean estado;
    private Long clienteId;
}
