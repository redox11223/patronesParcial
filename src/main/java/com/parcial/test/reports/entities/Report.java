package com.parcial.test.reports.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Report implements Prototype{
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 50)
  private String tipoReporte;

  @Column(nullable = false, length = 200)
  private String titulo;

  @Column(length = 500)
  private String encabezado;

  @Column(length = 500)
  private String pieReporte;

  @Column(length = 10)
  private String monedaCorporativa;

  @Column(length = 100)
  private String hashFirmaDigital;

  @Column(nullable = false)
  @Builder.Default
  private Boolean esPlantilla = false;

  @Column(nullable = false)
  @Builder.Default
  private LocalDate fechaGeneracion = LocalDate.now();

  @PrePersist
  public void prePersist() {
    if (this.fechaGeneracion == null) {
      this.fechaGeneracion = LocalDate.now();
    }
  }

  @Override
  public Prototype clone() {
    try {
      Report reportClone= (Report) super.clone();
      reportClone.fechaGeneracion=LocalDate.now();
      reportClone.id=null;
      reportClone.esPlantilla=false;
      reportClone.hashFirmaDigital=null;
      return reportClone;
    } catch (CloneNotSupportedException e) {
      throw new RuntimeException("Error al clonar el reporte",e);
    }
  }


}
