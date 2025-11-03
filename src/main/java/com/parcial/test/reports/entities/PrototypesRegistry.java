package com.parcial.test.reports.entities;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class PrototypesRegistry {

    private final Map<String, Prototype> plantillas = new HashMap<>();

    @PostConstruct
    public void init() {
        // Plantilla de Reporte Mensual
        Report reporteMensual = Report.builder()
                .tipoReporte("MENSUAL")
                .titulo("Reporte Mensual de Ingresos - FinanCorp S.A.")
                .encabezado("Consolidado de ventas e inventarios del mes")
                .pieReporte("Documento generado automáticamente por SERF")
                .monedaCorporativa("EUR")
                .esPlantilla(true)
                .build();
        plantillas.put("MENSUAL", reporteMensual);

        // Plantilla de Reporte Trimestral
        Report reporteTrimestral = Report.builder()
                .tipoReporte("TRIMESTRAL")
                .titulo("Reporte Trimestral de Gastos e Ingresos - FinanCorp S.A.")
                .encabezado("Análisis financiero trimestral consolidado")
                .pieReporte("Documento generado automáticamente por SERF")
                .monedaCorporativa("EUR")
                .esPlantilla(true)
                .build();
        plantillas.put("TRIMESTRAL", reporteTrimestral);

        // Plantilla de Reporte Anual
        Report reporteAnual = Report.builder()
                .tipoReporte("ANUAL")
                .titulo("Reporte Anual Consolidado - FinanCorp S.A.")
                .encabezado("Balance general anual de todas las filiales")
                .pieReporte("Documento generado automáticamente por SERF")
                .monedaCorporativa("EUR")
                .esPlantilla(true)
                .build();
        plantillas.put("ANUAL", reporteAnual);
    }

    public Prototype obtenerPlantilla(String tipo) {
        Prototype plantilla = plantillas.get(tipo.toUpperCase());
        if (plantilla == null) {
            throw new IllegalArgumentException("No existe plantilla para el tipo: " + tipo);
        }
        return plantilla.clone();
    }

    public void registrarPlantilla(String tipo, Prototype plantilla) {
        plantillas.put(tipo.toUpperCase(), plantilla);
    }
}

