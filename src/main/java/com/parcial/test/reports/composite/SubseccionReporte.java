package com.parcial.test.reports.composite;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubseccionReporte implements ComponenteReporte {
    
    private String titulo;
    private String contenido;

    public SubseccionReporte(String titulo, String contenido) {
        this.titulo = titulo;
        this.contenido = contenido;
    }

    @Override
    public String generar() {
        StringBuilder sb = new StringBuilder();
        
        sb.append("\n  ▶ ").append(titulo).append("\n");
        sb.append("  ").append("─".repeat(60)).append("\n");
        
        if (contenido != null && !contenido.isEmpty()) {
            // Indentar cada línea del contenido
            String[] lineas = contenido.split("\n");
            for (String linea : lineas) {
                sb.append("    ").append(linea).append("\n");
            }
        }



        return sb.toString();
    }

    @Override
    public void agregar(ComponenteReporte componente) {
        throw new UnsupportedOperationException("Una subsección no puede contener componentes hijos");
    }

    @Override
    public void eliminar(ComponenteReporte componente) {
        throw new UnsupportedOperationException("Una subsección no puede contener componentes hijos");
    }
}

