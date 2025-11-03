package com.parcial.test.sales.controllers;

import com.parcial.test.sales.entities.Sale;
import com.parcial.test.sales.services.SaleService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/v1/ventas")
@RequiredArgsConstructor
public class SaleController {

    private final SaleService saleService;

    @GetMapping
    public ResponseEntity<List<Sale>> getAllSales() {
        List<Sale> sales = saleService.getAll();
        return ResponseEntity.ok(sales);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Sale> getSale(@PathVariable Long id) {
        Sale sale = saleService.getById(id);
        return ResponseEntity.ok(sale);
    }

    @GetMapping("/pais/{pais}")
    public ResponseEntity<List<Sale>> getSalesByPais(@PathVariable String pais) {
        List<Sale> sales = saleService.getByPais(pais);
        return ResponseEntity.ok(sales);
    }

    @GetMapping("/fecha")
    public ResponseEntity<List<Sale>> getSalesByFecha(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        List<Sale> sales = saleService.getByFecha(inicio, fin);
        return ResponseEntity.ok(sales);
    }

    @GetMapping("/total-euros")
    public ResponseEntity<Double> getTotalVentasEnEuros() {
        Double total = saleService.getTotalVentasEnEuros();
        return ResponseEntity.ok(total);
    }

    @PostMapping
    public ResponseEntity<Sale> saveSale(@RequestBody Sale sale) {
        Sale newSale = saleService.save(sale);
        return ResponseEntity.status(HttpStatus.CREATED).body(newSale);
    }
}

