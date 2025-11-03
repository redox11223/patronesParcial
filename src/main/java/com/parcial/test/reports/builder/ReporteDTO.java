package com.parcial.test.reports.builder;

import com.parcial.test.reports.composite.ComponenteReporte;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class ReporteDTO {
    private String titulo;
    private String periodo;

    @Builder.Default
    private List<ComponenteReporte> secciones = new ArrayList<>();

    private String contenidoVentas;
    private String contenidoProductos;
    private String contenidoGraficos;
    private String conclusiones;

    public String generarContenidoCompleto() {
        StringBuilder sb = new StringBuilder();

        sb.append("═══════════════════════════════════════════════════════════\n");
        sb.append(titulo).append("\n");
        sb.append("Período: ").append(periodo).append("\n");
        sb.append("═══════════════════════════════════════════════════════════\n\n");

        // Generar secciones usando Composite
        for (ComponenteReporte seccion : secciones) {
            sb.append(seccion.generar()).append("\n");
        }

        // Contenido adicional
        if (contenidoVentas != null) {
            sb.append("\n").append(contenidoVentas).append("\n");
        }

        if (contenidoProductos != null) {
            sb.append("\n").append(contenidoProductos).append("\n");
        }

        if (contenidoGraficos != null) {
            sb.append("\n").append(contenidoGraficos).append("\n");
        }

        if (conclusiones != null) {
            sb.append("\n═══ CONCLUSIONES ═══\n");
            sb.append(conclusiones).append("\n");
        }

        sb.append("\n═══════════════════════════════════════════════════════════\n");
        sb.append("Documento generado automáticamente por SERF\n");
        sb.append("═══════════════════════════════════════════════════════════\n");

        return sb.toString();
    }
}

