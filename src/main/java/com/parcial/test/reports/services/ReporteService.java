package com.parcial.test.reports.services;

import com.parcial.test.config.CurrencyConversionService;
import com.parcial.test.products.entities.Product;
import com.parcial.test.products.services.ProductService;
import com.parcial.test.reports.builder.ReporteBuilderImpl;
import com.parcial.test.reports.builder.ReporteDTO;
import com.parcial.test.reports.composite.ComponenteReporte;
import com.parcial.test.reports.composite.SeccionReporte;
import com.parcial.test.reports.composite.SubseccionReporte;
import com.parcial.test.reports.decorator.FirmaDigitalDecorator;
import com.parcial.test.reports.decorator.MarcaAguaDecorator;
import com.parcial.test.reports.decorator.ReporteBase;
import com.parcial.test.reports.decorator.ReporteDecorator;
import com.parcial.test.reports.entities.PrototypesRegistry;
import com.parcial.test.reports.entities.Report;
import com.parcial.test.sales.entities.Sale;
import com.parcial.test.sales.services.SaleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReporteService {

    private final PrototypesRegistry prototypesRegistry;
    private final ReporteBuilderImpl reporteBuilder;
    private final SaleService saleService;
    private final ProductService productService;
    private final CurrencyConversionService currencyConversionService;
    private final com.parcial.test.clients.services.ClientService clientService;

    /**
     * Patrón FACADE: Este método oculta toda la complejidad de generación de reportes
     */
    public String generarReporteMensual(int mes, int anio) {
        // 1. PROTOTYPE: Clonar plantilla mensual
        Report plantilla = (Report) prototypesRegistry.obtenerPlantilla("MENSUAL");

        String periodo = String.format("%02d-%d", mes, anio);

        // Obtener datos del mes
        LocalDate inicio = LocalDate.of(anio, mes, 1);
        LocalDate fin = inicio.plusMonths(1).minusDays(1);
        List<Sale> ventas = saleService.getByFecha(inicio, fin);

        // 2. COMPOSITE: Crear estructura jerárquica del reporte
        ComponenteReporte estructuraCompleta = crearEstructuraComposite(ventas, inicio, fin);

        // 3. BUILDER: Construir el reporte paso a paso
        ReporteDTO reporteDTO = reporteBuilder
                .conTitulo(plantilla.getTitulo())
                .conPeriodo(periodo)
                .conSeccion(estructuraCompleta)
                .conConclusiones(generarConclusiones(ventas))
                .construir();

        String contenido = reporteDTO.generarContenidoCompleto();

        // 4. DECORATOR: Aplicar marca de agua y firma digital
        ReporteDecorator reporteDecorado = new ReporteBase(contenido);
        reporteDecorado = new MarcaAguaDecorator(reporteDecorado);
        reporteDecorado = new FirmaDigitalDecorator(reporteDecorado);

        return reporteDecorado.obtenerContenido();
    }

    public String generarReporteTrimestral(int trimestre, int anio) {
        Report plantilla = (Report) prototypesRegistry.obtenerPlantilla("TRIMESTRAL");

        int mesInicio = (trimestre - 1) * 3 + 1;
        LocalDate inicio = LocalDate.of(anio, mesInicio, 1);
        LocalDate fin = inicio.plusMonths(3).minusDays(1);

        List<Sale> ventas = saleService.getByFecha(inicio, fin);

        ComponenteReporte estructuraCompleta = crearEstructuraComposite(ventas, inicio, fin);

        ReporteDTO reporteDTO = reporteBuilder
                .conTitulo(plantilla.getTitulo())
                .conPeriodo("Q" + trimestre + "-" + anio)
                .conSeccion(estructuraCompleta)
                .conConclusiones(generarConclusiones(ventas))
                .construir();

        String contenido = reporteDTO.generarContenidoCompleto();

        ReporteDecorator reporteDecorado = new ReporteBase(contenido);
        reporteDecorado = new MarcaAguaDecorator(reporteDecorado);
        reporteDecorado = new FirmaDigitalDecorator(reporteDecorado);

        return reporteDecorado.obtenerContenido();
    }

    public String generarReporteAnual(int anio) {
        Report plantilla = (Report) prototypesRegistry.obtenerPlantilla("ANUAL");

        LocalDate inicio = LocalDate.of(anio, 1, 1);
        LocalDate fin = LocalDate.of(anio, 12, 31);

        List<Sale> ventas = saleService.getByFecha(inicio, fin);

        ComponenteReporte estructuraCompleta = crearEstructuraComposite(ventas, inicio, fin);

        ReporteDTO reporteDTO = reporteBuilder
                .conTitulo(plantilla.getTitulo())
                .conPeriodo(String.valueOf(anio))
                .conSeccion(estructuraCompleta)
                .conConclusiones(generarConclusiones(ventas))
                .construir();

        String contenido = reporteDTO.generarContenidoCompleto();

        ReporteDecorator reporteDecorado = new ReporteBase(contenido);
        reporteDecorado = new MarcaAguaDecorator(reporteDecorado);
        reporteDecorado = new FirmaDigitalDecorator(reporteDecorado);

        return reporteDecorado.obtenerContenido();
    }

    /**
     * COMPOSITE: Crear estructura jerárquica de secciones y subsecciones
     */
    private ComponenteReporte crearEstructuraComposite(List<Sale> ventas, LocalDate inicio, LocalDate fin) {
        SeccionReporte raiz = new SeccionReporte("REPORTE CONSOLIDADO FINANCIERO");

        // Sección 1: Resumen Ejecutivo
        SeccionReporte seccionResumen = new SeccionReporte("Resumen Ejecutivo");
        seccionResumen.agregar(new SubseccionReporte(
                "Período Analizado",
                inicio.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " - " +
                fin.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        ));
        seccionResumen.agregar(new SubseccionReporte(
                "Total de Transacciones",
                String.valueOf(ventas.size()) + " ventas registradas"
        ));

        Double totalEuros = ventas.stream()
                .mapToDouble(v -> currencyConversionService.convertirAMonedaCorporativa(
                        v.getMontoTotal(), v.getMonedaLocal()))
                .sum();

        seccionResumen.agregar(new SubseccionReporte(
                "Ingresos Totales (EUR)",
                String.format("€ %.2f", totalEuros)
        ));

        raiz.agregar(seccionResumen);

        // Sección 2: Ventas por País
        SeccionReporte seccionPaises = new SeccionReporte("Ingresos por País/Filial");
        Map<String, Double> ventasPorPais = ventas.stream()
                .collect(Collectors.groupingBy(
                        Sale::getPaisFilial,
                        Collectors.summingDouble(v ->
                                currencyConversionService.convertirAMonedaCorporativa(
                                        v.getMontoTotal(), v.getMonedaLocal()))
                ));

        ventasPorPais.forEach((pais, total) -> {
            seccionPaises.agregar(new SubseccionReporte(
                    pais,
                    String.format("Total: € %.2f | Porcentaje: %.1f%%",
                            total, (total / totalEuros * 100))
            ));
        });

        raiz.agregar(seccionPaises);

        // Sección 3: Productos más vendidos
        SeccionReporte seccionProductos = new SeccionReporte("Análisis de Productos");
        List<Product> productos = productService.getAll();

        StringBuilder contenidoProductos = new StringBuilder();
        contenidoProductos.append(String.format("%-30s | %-15s | %-10s\n",
                "Producto", "Categoría", "Stock Actual"));
        contenidoProductos.append("-".repeat(65)).append("\n");

        productos.stream().limit(10).forEach(p -> {
            contenidoProductos.append(String.format("%-30s | %-15s | %10d\n",
                    p.getNombre().substring(0, Math.min(30, p.getNombre().length())),
                    p.getCategoriaProducto(),
                    p.getStock()));
        });

        seccionProductos.agregar(new SubseccionReporte(
                "Inventario Actual",
                contenidoProductos.toString()
        ));

        raiz.agregar(seccionProductos);

        // Sección 4: Métodos de Pago
        SeccionReporte seccionPagos = new SeccionReporte("Métodos de Pago Utilizados");
        Map<String, Long> pagosPorMetodo = ventas.stream()
                .collect(Collectors.groupingBy(
                        v -> v.getMetodoPago().toString(),
                        Collectors.counting()
                ));

        pagosPorMetodo.forEach((metodo, cantidad) -> {
            seccionPagos.agregar(new SubseccionReporte(
                    metodo,
                    String.format("%d transacciones (%.1f%%)",
                            cantidad, (cantidad * 100.0 / ventas.size()))
            ));
        });

        raiz.agregar(seccionPagos);

        return raiz;
    }

    private String generarConclusiones(List<Sale> ventas) {
        StringBuilder conclusiones = new StringBuilder();

        if (ventas.isEmpty()) {
            conclusiones.append("No se registraron ventas en el período analizado.\n");
            return conclusiones.toString();
        }

        Double totalEuros = ventas.stream()
                .mapToDouble(v -> currencyConversionService.convertirAMonedaCorporativa(
                        v.getMontoTotal(), v.getMonedaLocal()))
                .sum();

        conclusiones.append("1. El período analizado registró un total de ")
                .append(ventas.size()).append(" transacciones.\n\n");

        conclusiones.append("2. Los ingresos consolidados alcanzaron € ")
                .append(String.format("%.2f", totalEuros))
                .append(" (EUR - Moneda Corporativa).\n\n");

        Map<String, Long> ventasPorPais = ventas.stream()
                .collect(Collectors.groupingBy(Sale::getPaisFilial, Collectors.counting()));

        String paisMasActivo = ventasPorPais.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("N/A");

        conclusiones.append("3. La filial con mayor actividad fue: ")
                .append(paisMasActivo).append(".\n\n");

        conclusiones.append("4. El sistema SERF garantiza la consolidación automática de datos\n");
        conclusiones.append("   en moneda corporativa (EUR) para facilitar la toma de decisiones.\n\n");

        conclusiones.append("5. Todos los reportes generados incluyen firma digital y marca de agua\n");
        conclusiones.append("   para garantizar la autenticidad e integridad del documento.\n");

        return conclusiones.toString();
    }

    /**
     * Generar reporte de productos en stock
     */
    public List<Product> generarReporteProductosStock() {
        return productService.getAll();
    }

    /**
     * Generar reporte de clientes activos
     */
    public List<com.parcial.test.clients.entities.Client> generarReporteClientesActivos() {
        return clientService.getAll();
    }

    /**
     * Obtener ventas del mes actual para el frontend
     */
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public List<Sale> obtenerVentasMensuales() {
        java.time.LocalDate hoy = java.time.LocalDate.now();
        java.time.LocalDate inicio = hoy.withDayOfMonth(1);
        java.time.LocalDate fin = hoy.withDayOfMonth(hoy.lengthOfMonth());

        // Usar getAll que carga todas las relaciones, y luego filtrar por fecha
        List<Sale> todasLasVentas = saleService.getAll();

        // Filtrar por rango de fechas
        java.time.ZoneId zoneId = java.time.ZoneId.systemDefault();
        java.util.Date fechaInicio = java.util.Date.from(inicio.atStartOfDay(zoneId).toInstant());
        java.util.Date fechaFin = java.util.Date.from(fin.atTime(23, 59, 59).atZone(zoneId).toInstant());

        return todasLasVentas.stream()
                .filter(venta -> venta.getFechaVenta() != null &&
                        !venta.getFechaVenta().before(fechaInicio) &&
                        !venta.getFechaVenta().after(fechaFin))
                .collect(java.util.stream.Collectors.toList());
    }
}

