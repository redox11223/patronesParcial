package com.parcial.test.reports.builder;

import com.parcial.test.reports.composite.ComponenteReporte;

public interface ReporteBuilder {
    ReporteBuilder conTitulo(String titulo);
    ReporteBuilder conPeriodo(String periodo);
    ReporteBuilder conSeccion(ComponenteReporte seccion);
    ReporteBuilder conSeccionVentas(String contenido);
    ReporteBuilder conSeccionProductos(String contenido);
    ReporteBuilder conSeccionGraficos(String contenido);
    ReporteBuilder conConclusiones(String conclusiones);
    ReporteDTO construir();
}
