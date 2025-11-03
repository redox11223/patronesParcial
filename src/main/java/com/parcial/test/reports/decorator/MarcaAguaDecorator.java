package com.parcial.test.reports.decorator;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MarcaAguaDecorator implements ReporteDecorator {
    
    private final ReporteDecorator reporte;

    @Override
    public String obtenerContenido() {
        String contenidoOriginal = reporte.obtenerContenido();
        
        StringBuilder sb = new StringBuilder();
        
        // Agregar marca de agua en la parte superior
        sb.append("\n");
        sb.append("╔════════════════════════════════════════════════════════════════════╗\n");
        sb.append("║                    *** CONFIDENCIAL ***                            ║\n");
        sb.append("║              FinanCorp S.A. - Documento Interno                    ║\n");
        sb.append("║           Prohibida su distribución no autorizada                  ║\n");
        sb.append("╚════════════════════════════════════════════════════════════════════╝\n");
        sb.append("\n");
        
        // Contenido original con marca de agua lateral cada cierto número de líneas
        String[] lineas = contenidoOriginal.split("\n");
        for (int i = 0; i < lineas.length; i++) {
            sb.append(lineas[i]);
            
            // Agregar marca de agua lateral cada 10 líneas
            if (i % 10 == 0 && i > 0) {
                sb.append("    [CONFIDENCIAL]");
            }
            
            sb.append("\n");
        }
        
        // Agregar marca de agua en la parte inferior
        sb.append("\n");
        sb.append("╔════════════════════════════════════════════════════════════════════╗\n");
        sb.append("║                    *** CONFIDENCIAL ***                            ║\n");
        sb.append("║              Este documento contiene información sensible          ║\n");
        sb.append("╚════════════════════════════════════════════════════════════════════╝\n");
        
        return sb.toString();
    }
}

