package com.novabank.cuenta.exception;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class ErrorResponse {

    private String mensaje;
    private int codigo;
    private LocalDateTime timestamp;

    public ErrorResponse() {
    }

    public ErrorResponse(String mensaje, int codigo, LocalDateTime timestamp) {
        this.mensaje = mensaje;
        this.codigo = codigo;
        this.timestamp = timestamp;
    }

}
