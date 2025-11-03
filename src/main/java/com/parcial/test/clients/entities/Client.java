package com.parcial.test.clients.entities;

import com.parcial.test.sales.entities.Sale;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Client {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String nombre;

  @Column(nullable = false)
  private String documento;

  @Column(length = 20)
  private String telefono;

  @Column(nullable = false,length = 50)
  private String pais;

  @OneToMany(mappedBy = "cliente",cascade = CascadeType.ALL,orphanRemoval = true)
  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  @Builder.Default
  private List<Sale> ventas=new ArrayList<>();

  public void agregarVenta(Sale venta){
    ventas.add(venta);
    venta.setCliente(this);
  }

  public void eliminarVenta(Sale venta){
    ventas.remove(venta);
    venta.setCliente(null);
  }
}
