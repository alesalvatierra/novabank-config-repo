package com.novabank.operacion.dto;

import lombok.Data;

@Data
public class CuentaDTO {
    private Long id;
    private String numeroCuenta;
    private Double saldo;
    private Boolean estado;
    private Long clienteId;
}
