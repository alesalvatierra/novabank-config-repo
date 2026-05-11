package com.novabank.exchangerate.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExchangeRateResponse {
    private String fromCurrency;
    private String toCurrency;
    private Double rate;
}
