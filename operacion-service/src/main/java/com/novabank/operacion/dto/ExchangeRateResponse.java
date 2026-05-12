package com.novabank.operacion.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeRateResponse {

    private String fromCurrency;
    private String toCurrency;
    private Double rate;
}
