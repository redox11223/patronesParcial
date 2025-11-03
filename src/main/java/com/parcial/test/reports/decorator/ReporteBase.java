package com.parcial.test.reports.decorator;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ReporteBase implements ReporteDecorator {
    
    private String contenido;

    @Override
    public String obtenerContenido() {
        return contenido;
    }
}

