package com.parcial.test.reports.controllers;

import com.parcial.test.reports.services.ReporteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/reportes")
@RequiredArgsConstructor
public class ReporteController {

    private final ReporteService reporteService;

    @GetMapping("/mensual")
    public ResponseEntity<String> generarReporteMensual(
            @RequestParam int mes,
            @RequestParam int anio) {

        String reporte = reporteService.generarReporteMensual(mes, anio);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        headers.set("Content-Disposition",
                String.format("inline; filename=reporte_mensual_%02d_%d.txt", mes, anio));

        return ResponseEntity.ok()
                .headers(headers)
                .body(reporte);
    }

    @GetMapping("/trimestral")
    public ResponseEntity<String> generarReporteTrimestral(
            @RequestParam int trimestre,
            @RequestParam int anio) {

        if (trimestre < 1 || trimestre > 4) {
            return ResponseEntity.badRequest()
                    .body("El trimestre debe estar entre 1 y 4");
        }

        String reporte = reporteService.generarReporteTrimestral(trimestre, anio);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        headers.set("Content-Disposition",
                String.format("inline; filename=reporte_trimestral_Q%d_%d.txt", trimestre, anio));

        return ResponseEntity.ok()
                .headers(headers)
                .body(reporte);
    }

    @GetMapping("/anual")
    public ResponseEntity<String> generarReporteAnual(@RequestParam int anio) {

        String reporte = reporteService.generarReporteAnual(anio);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        headers.set("Content-Disposition",
                String.format("inline; filename=reporte_anual_%d.txt", anio));

        return ResponseEntity.ok()
                .headers(headers)
                .body(reporte);
    }

    @GetMapping("/productos-stock")
    public ResponseEntity<?> generarReporteProductosStock() {
        var productos = reporteService.generarReporteProductosStock();
        return ResponseEntity.ok(productos);
    }

    @GetMapping("/clientes-activos")
    public ResponseEntity<?> generarReporteClientesActivos() {
        var clientes = reporteService.generarReporteClientesActivos();
        return ResponseEntity.ok(clientes);
    }

    @GetMapping("/ventas-mensuales")
    public ResponseEntity<?> generarReporteVentasMensuales() {
        var ventas = reporteService.obtenerVentasMensuales();
        // Convertir a DTOs para el frontend
        var ventasDTO = ventas.stream()
                .map(venta -> {
                    if (venta.getClienteId() == null && venta.getCliente() != null) {
                        venta.setClienteId(venta.getCliente().getId());
                    }
                    return com.parcial.test.sales.dto.SaleDTO.fromEntity(venta);
                })
                .collect(java.util.stream.Collectors.toList());
        return ResponseEntity.ok(ventasDTO);
    }
}

