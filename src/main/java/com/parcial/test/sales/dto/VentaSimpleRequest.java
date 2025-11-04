package com.parcial.test.sales.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VentaSimpleRequest {
    private Long clienteId;
    private Long productoId;
    private Integer cantidad;
}

