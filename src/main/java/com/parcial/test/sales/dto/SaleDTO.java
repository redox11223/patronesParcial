package com.parcial.test.sales.dto;

import com.parcial.test.sales.entities.Sale;
import com.parcial.test.sales.entities.SaleDetail;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaleDTO {
    private Long id;
    private String numeroFactura;
    private Long clienteId;
    private String clienteNombre;
    private Long productoId;
    private String productoNombre;
    private Integer cantidad;
    private Double montoTotal;
    private Date fechaVenta;
    private String paisFilial;

    public static SaleDTO fromEntity(Sale sale) {
        SaleDTOBuilder builder = SaleDTO.builder()
                .id(sale.getId())
                .numeroFactura(sale.getNumeroFactura())
                .montoTotal(sale.getMontoTotal())
                .fechaVenta(sale.getFechaVenta())
                .paisFilial(sale.getPaisFilial());

        // Agregar información del cliente
        try {
            if (sale.getCliente() != null) {
                builder.clienteId(sale.getCliente().getId());
                builder.clienteNombre(sale.getCliente().getNombre());
            } else if (sale.getClienteId() != null) {
                builder.clienteId(sale.getClienteId());
            }
        } catch (Exception e) {
            System.err.println("Error al cargar cliente para venta ID: " + sale.getId() + " - " + e.getMessage());
            if (sale.getClienteId() != null) {
                builder.clienteId(sale.getClienteId());
            }
        }

        // Si hay detalles de venta, tomar el primero (para ventas simples)
        try {
            if (sale.getSales() != null && !sale.getSales().isEmpty()) {
                SaleDetail detalle = sale.getSales().get(0);
                if (detalle != null) {
                    builder.cantidad(detalle.getCantidad());
                    try {
                        if (detalle.getProducto() != null) {
                            builder.productoId(detalle.getProducto().getId())
                                   .productoNombre(detalle.getProducto().getNombre());
                        } else {
                            System.err.println("Producto es null en detalle de venta ID: " + sale.getId());
                        }
                    } catch (Exception pe) {
                        System.err.println("Error al cargar producto para venta ID: " + sale.getId() + " - " + pe.getMessage());
                    }
                } else {
                    System.err.println("Detalle de venta es null para venta ID: " + sale.getId());
                }
            } else {
                System.err.println("No hay detalles de venta para venta ID: " + sale.getId());
            }
        } catch (Exception e) {
            System.err.println("Error general al cargar detalles de venta para ID: " + sale.getId() + " - " + e.getMessage());
            e.printStackTrace();
        }

        return builder.build();
    }
}

