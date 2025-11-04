package com.parcial.test.sales.services;

import com.parcial.test.config.CurrencyConversionService;
import com.parcial.test.exceptions.BusinessLogicException;
import com.parcial.test.exceptions.ResourceNotFoundException;
import com.parcial.test.exceptions.ValidationException;
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
    private final com.parcial.test.clients.ClienteRepo clienteRepo;

    @Override
    @Transactional
    public Sale save(Sale sale) {
        validateSale(sale);

        // Validar y actualizar stock de productos
        for (SaleDetail detalle : sale.getSales()) {
            Product producto = productRepo.findById(detalle.getProducto().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Producto", String.valueOf(detalle.getProducto().getId())));

            // Verificar stock disponible
            if (!producto.haystock(detalle.getCantidad())) {
                throw new BusinessLogicException(
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
    @Transactional(readOnly = true)
    public List<Sale> getAll() {
        // Usar el método que carga todas las relaciones con JOIN FETCH
        return salesRepo.findAllWithDetails();
    }

    @Override
    public Sale getById(Long id) {
        return salesRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venta", String.valueOf(id)));
    }

    @Override
    public List<Sale> getByPais(String pais) {
        if (pais == null || pais.trim().isEmpty()) {
            throw new ValidationException("pais", "El país no puede estar vacío");
        }
        return salesRepo.findByPaisFilial(pais);
    }

    @Override
    public List<Sale> getByFecha(LocalDate inicio, LocalDate fin) {
        if (inicio == null || fin == null) {
            throw new ValidationException("fecha", "Las fechas de inicio y fin son obligatorias");
        }
        if (inicio.isAfter(fin)) {
            throw new ValidationException("fecha", "La fecha de inicio debe ser anterior a la fecha de fin");
        }
        Date fechaInicio = Date.from(inicio.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date fechaFin = Date.from(fin.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant());
        return salesRepo.findByFechaVentaBetween(fechaInicio, fechaFin);
    }

    @Override
    public Double getTotalVentasEnEuros() {
        List<Sale> ventas = salesRepo.findAll();
        return ventas.stream()
                .mapToDouble(sale -> {
                    try {
                        return currencyConversionService.convertirAMonedaCorporativa(
                            sale.getMontoTotal(),
                            sale.getMonedaLocal()
                        );
                    } catch (IllegalArgumentException e) {
                        throw new BusinessLogicException("Error al calcular total de ventas: " + e.getMessage(), e);
                    }
                })
                .sum();
    }

    @Override
    @Transactional
    public Sale crearVentaSimple(Long clienteId, Long productoId, Integer cantidad) {
        // Validar parámetros
        if (cantidad == null || cantidad <= 0) {
            throw new ValidationException("cantidad", "La cantidad debe ser mayor a 0");
        }

        // Buscar cliente
        com.parcial.test.clients.entities.Client cliente = clienteRepo.findById(clienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", String.valueOf(clienteId)));

        // Buscar producto
        Product producto = productRepo.findById(productoId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", String.valueOf(productoId)));

        // Verificar stock
        if (!producto.haystock(cantidad)) {
            throw new BusinessLogicException(
                "Stock insuficiente para producto: " + producto.getNombre() +
                ". Disponible: " + producto.getStock() +
                ", Solicitado: " + cantidad
            );
        }

        // Obtener precio del producto (usar costo corporativo o el de origen)
        Double precio = producto.getCostoImportacionCorp() != null ?
                        producto.getCostoImportacionCorp() :
                        producto.getCostoImportacionOrigen();

        // Crear detalle de venta
        SaleDetail detalle = SaleDetail.crear(producto, cantidad, precio);

        // Crear venta
        Sale venta = Sale.builder()
                .numeroFactura("FAC-" + System.currentTimeMillis())
                .cliente(cliente)
                .metodoPago(com.parcial.test.sales.entities.MetodoPago.EFECTIVO)
                .monedaLocal(producto.getMonedaOrigen())
                .vendedorResponsable("Sistema")
                .paisFilial(cliente.getPais())
                .fechaVenta(new Date())
                .build();

        // Agregar detalle a la venta
        venta.AgregarDetalle(detalle);

        // Disminuir stock
        producto.disminuirStock(cantidad);
        productRepo.save(producto);

        // Calcular total y guardar
        venta.calcularMontoTotal();
        Sale ventaGuardada = salesRepo.save(venta);

        // Establecer clienteId para el frontend
        ventaGuardada.setClienteId(clienteId);

        return ventaGuardada;
    }

    private void validateSale(Sale sale) {
        if (sale.getNumeroFactura() == null || sale.getNumeroFactura().trim().isEmpty()) {
            throw new ValidationException("numeroFactura", "El número de factura es obligatorio");
        }
        if (sale.getCliente() == null) {
            throw new ValidationException("cliente", "El cliente es obligatorio");
        }
        if (sale.getMetodoPago() == null) {
            throw new ValidationException("metodoPago", "El método de pago es obligatorio");
        }
        if (sale.getMonedaLocal() == null) {
            throw new ValidationException("monedaLocal", "La moneda local es obligatoria");
        }
        if (sale.getVendedorResponsable() == null || sale.getVendedorResponsable().trim().isEmpty()) {
            throw new ValidationException("vendedorResponsable", "El vendedor responsable es obligatorio");
        }
        if (sale.getPaisFilial() == null || sale.getPaisFilial().trim().isEmpty()) {
            throw new ValidationException("paisFilial", "El país de la filial es obligatorio");
        }
        if (sale.getSales() == null || sale.getSales().isEmpty()) {
            throw new ValidationException("sales", "La venta debe tener al menos un detalle");
        }
    }
}
