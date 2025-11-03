package com.parcial.test.reports.composite;

public interface ComponenteReporte {
    String generar();
    void agregar(ComponenteReporte componente);
    void eliminar(ComponenteReporte componente);
}

