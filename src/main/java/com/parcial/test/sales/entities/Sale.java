package com.parcial.test.sales.entities;

import com.parcial.test.clients.entities.Client;
import com.parcial.test.products.entities.MonedaOrigen;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sale {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false,unique = true)
  private String numeroFactura;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(nullable = false,name = "cliente_id")
  private Client cliente;

  @OneToMany(mappedBy = "venta",cascade = CascadeType.ALL,orphanRemoval = true)
  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  @Builder.Default
  private List<SaleDetail> sales=new ArrayList<>();

  @Enumerated(EnumType.STRING)
  @Column(nullable = false,length = 50)
  private MetodoPago metodoPago;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false,length = 10)
  private MonedaOrigen monedaLocal;

  @Column(nullable = false, length = 100)
  private String vendedorResponsable;

  @Column(nullable = false)
  private Double MontoTotal;

  @Column(nullable = false, length = 50)
  private String paisFilial;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(nullable = false)
  private Date fechaVenta;

  @PrePersist
  protected void onCreate(){
    fechaVenta=new Date();
  }

  @PrePersist
  @PreUpdate
  public void calcularMontoTotal(){
    this.MontoTotal=sales.stream().mapToDouble(SaleDetail::getSubtotal).sum();
  }

  //helpers
  public void AgregarDetalle(SaleDetail detalle){
    sales.add(detalle);
    detalle.setVenta(this);
  }
  public void EliminarDetalle(SaleDetail detalle){
    sales.remove(detalle);
    detalle.setVenta(null);
  }
}
