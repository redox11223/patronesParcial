package com.parcial.test.reports.entities;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
public class PrototypesRegistry {
  private Map<String,Prototype> prototypes= new HashMap<>();

  @PostConstruct
  public void initReportsPrototypes(){
    Report reporteMensual= Report.builder()
            .tipoReporte("Mensual")
            .titulo("Reporte Financiero Mensual")
            .encabezado("FinanCorp S.A. - Consolidado Mensual")
            .pieReporte("FinanCorp S.A. - Documento base")
            .esPlantilla(true)
            .build();
    Report reporteAnual= Report.builder()
            .tipoReporte("Anual")
            .titulo("Reporte Financiero Anual")
            .encabezado("FinanCorp S.A. - Consolidado Anual")
            .pieReporte("FinanCorp S.A. - Documento base")
            .esPlantilla(true)
            .build();
    Report reporteTrimestral= Report.builder()
            .tipoReporte("Trimestral")
            .titulo("Reporte Financiero Trimestral")
            .encabezado("FinanCorp S.A. - Consolidado Trimestral")
            .pieReporte("FinanCorp S.A. - Documento base")
            .esPlantilla(true)
            .build();

    registrarPrototype("Mensual",reporteMensual);
    registrarPrototype("Anual",reporteAnual);
    registrarPrototype("Trimestral",reporteTrimestral);
  }


  public void registrarPrototype(String nombre,Prototype prototype){
     prototypes.put(nombre,prototype);
  }

  public Prototype obtenerPrototype(String tipo){
    Prototype prototype=prototypes.get(tipo);
    if(prototype==null){
      throw new IllegalArgumentException("No existe platilla para tipo: "+tipo+
              ". Tipos disponibles: " + prototypes.keySet()
      );
    }
    return prototype.clone();
  }

  public Set<String> listarTiposDisponibles(){
    return prototypes.keySet();
  }
}
