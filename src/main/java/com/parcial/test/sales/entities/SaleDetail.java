package com.parcial.test.sales.entities;

import com.parcial.test.products.entities.Product;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "sale_details")
public class SaleDetail {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "id_venta",nullable = false)
  private Sale venta;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "id_producto",nullable = false)
  private Product producto;

  @Column(nullable = false)
  private Integer cantidad;

  @Column(nullable = false)
  private Double precioUnitario;

  @Column(nullable = false)
  private Double subtotal;

  @PrePersist
  @PreUpdate
  public void calcularSubTotal(){
    if(cantidad!=null && precioUnitario!=null){
      this.subtotal=cantidad*precioUnitario;
    }
  }

  public static SaleDetail crear(Product product,int cantidad,Double precio){
    SaleDetail saleDetail=new SaleDetail();
    saleDetail.setProducto(product);
    saleDetail.setCantidad(cantidad);
    saleDetail.setPrecioUnitario(precio);
    saleDetail.calcularSubTotal();

    return saleDetail;
  }
}
