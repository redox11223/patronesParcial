package com.parcial.test.products.entities;

import com.parcial.test.sales.entities.SaleDetail;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "productos")
@Builder
public class Product {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false,unique = true)
  private String codigo;

  @Column(nullable = false,length = 100)
  private String nombre;

  @Column(length = 500)
  private String descripcion;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false,length = 50)
  private CategoriaProducto categoriaProducto;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false,length = 10)
  private MonedaOrigen monedaOrigen;

  @OneToMany(mappedBy = "producto",cascade = CascadeType.ALL, orphanRemoval = false)
  @Builder.Default
  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  @com.fasterxml.jackson.annotation.JsonIgnore
  private List<SaleDetail> detalleVenta=new ArrayList<>();

  @Column(nullable = false)
  private Double costoImportacionOrigen;

  @Column
  private Double costoImportacionCorp;

  @Column(nullable = false)
  private Integer stock;

  //Metodos para el stock
  public boolean haystock(int cantidad){
    return this.stock>=cantidad;
  }
  public void aumentarStock(int cantidad){
    this.stock+=cantidad;
  }

  public void disminuirStock(int cantidad){
    if (this.stock>=cantidad){
      this.stock-=cantidad;
    }else {
      throw new IllegalStateException(
              "Stock insuficiente. Disponible: "+this.stock+
                      " Solicitado: "+ cantidad
      );
    }
  }

  private String proveedor;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(nullable = false)
  private Date fechaImportacion;

  @PrePersist
  protected void onCreate(){
    fechaImportacion=new Date();
  }

}
