package com.parcial.test.reports.builder;

import com.parcial.test.reports.composite.ComponenteReporte;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ReporteBuilderImpl implements ReporteBuilder {

    private String titulo;
    private String periodo;
    private List<ComponenteReporte> secciones = new ArrayList<>();
    private String contenidoVentas;
    private String contenidoProductos;
    private String contenidoGraficos;
    private String conclusiones;

    @Override
    public ReporteBuilder conTitulo(String titulo) {
        this.titulo = titulo;
        return this;
    }

    @Override
    public ReporteBuilder conPeriodo(String periodo) {
        this.periodo = periodo;
        return this;
    }

    @Override
    public ReporteBuilder conSeccion(ComponenteReporte seccion) {
        this.secciones.add(seccion);
        return this;
    }

    @Override
    public ReporteBuilder conSeccionVentas(String contenido) {
        this.contenidoVentas = contenido;
        return this;
    }

    @Override
    public ReporteBuilder conSeccionProductos(String contenido) {
        this.contenidoProductos = contenido;
        return this;
    }

    @Override
    public ReporteBuilder conSeccionGraficos(String contenido) {
        this.contenidoGraficos = contenido;
        return this;
    }

    @Override
    public ReporteBuilder conConclusiones(String conclusiones) {
        this.conclusiones = conclusiones;
        return this;
    }

    @Override
    public ReporteDTO construir() {
        ReporteDTO reporte = ReporteDTO.builder()
                .titulo(this.titulo)
                .periodo(this.periodo)
                .secciones(new ArrayList<>(this.secciones))
                .contenidoVentas(this.contenidoVentas)
                .contenidoProductos(this.contenidoProductos)
                .contenidoGraficos(this.contenidoGraficos)
                .conclusiones(this.conclusiones)
                .build();

        // Resetear el builder para nueva construcción
        reset();

        return reporte;
    }

    private void reset() {
        this.titulo = null;
        this.periodo = null;
        this.secciones = new ArrayList<>();
        this.contenidoVentas = null;
        this.contenidoProductos = null;
        this.contenidoGraficos = null;
        this.conclusiones = null;
    }
}

