package com.parcial.test.sales.services;

import com.parcial.test.config.CurrencyConversionService;
import com.parcial.test.products.entities.Product;
import com.parcial.test.products.repository.ProductRepo;
import com.parcial.test.sales.entities.Sale;
import com.parcial.test.sales.entities.SaleDetail;
import com.parcial.test.sales.repository.SalesRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SaleServiceImpl implements SaleService {

    private final SalesRepo salesRepo;
    private final ProductRepo productRepo;
    private final CurrencyConversionService currencyConversionService;

    @Override
    @Transactional
    public Sale save(Sale sale) {
        // Validar y actualizar stock de productos
        for (SaleDetail detalle : sale.getSales()) {
            Product producto = productRepo.findById(detalle.getProducto().getId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + detalle.getProducto().getId()));

            // Verificar stock disponible
            if (!producto.haystock(detalle.getCantidad())) {
                throw new IllegalStateException(
                    "Stock insuficiente para producto: " + producto.getNombre() +
                    ". Disponible: " + producto.getStock() +
                    ", Solicitado: " + detalle.getCantidad()
                );
            }

            // Disminuir stock
            producto.disminuirStock(detalle.getCantidad());
            productRepo.save(producto);

            // Establecer relación bidireccional
            detalle.setVenta(sale);
        }

        // Calcular monto total antes de guardar
        sale.calcularMontoTotal();

        return salesRepo.save(sale);
    }

    @Override
    public List<Sale> getAll() {
        return salesRepo.findAll();
    }

    @Override
    public Sale getById(Long id) {
        return salesRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada: " + id));
    }

    @Override
    public List<Sale> getByPais(String pais) {
        return salesRepo.findByPaisFilial(pais);
    }

    @Override
    public List<Sale> getByFecha(LocalDate inicio, LocalDate fin) {
        Date fechaInicio = Date.from(inicio.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date fechaFin = Date.from(fin.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant());
        return salesRepo.findByFechaVentaBetween(fechaInicio, fechaFin);
    }

    @Override
    public Double getTotalVentasEnEuros() {
        List<Sale> ventas = salesRepo.findAll();
        return ventas.stream()
                .mapToDouble(sale -> currencyConversionService.convertirAMonedaCorporativa(
                        sale.getMontoTotal(),
                        sale.getMonedaLocal()
                ))
                .sum();
    }
}

