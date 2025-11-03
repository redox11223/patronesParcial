package com.parcial.test.reports.composite;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class SeccionReporte implements ComponenteReporte {

    private String titulo;
    private String contenido;
    private List<ComponenteReporte> subsecciones = new ArrayList<>();

    public SeccionReporte(String titulo) {
        this.titulo = titulo;
    }

    public SeccionReporte(String titulo, String contenido) {
        this.titulo = titulo;
        this.contenido = contenido;
    }

    @Override
    public String generar() {
        StringBuilder sb = new StringBuilder();

        sb.append("\n╔═══════════════════════════════════════════════════════════╗\n");
        sb.append("  ").append(titulo.toUpperCase()).append("\n");
        sb.append("╚═══════════════════════════════════════════════════════════╝\n");

        if (contenido != null && !contenido.isEmpty()) {
            sb.append(contenido).append("\n");
        }

        // Generar subsecciones recursivamente
        for (ComponenteReporte subseccion : subsecciones) {
            sb.append(subseccion.generar());
        }

        return sb.toString();
    }

    @Override
    public void agregar(ComponenteReporte componente) {
        subsecciones.add(componente);
    }

    @Override
    public void eliminar(ComponenteReporte componente) {
        subsecciones.remove(componente);
    }
}

