package com.parcial.test.sales.services;

import com.parcial.test.sales.entities.Sale;

import java.time.LocalDate;
import java.util.List;

public interface SaleService {
    Sale save(Sale sale);
    List<Sale> getAll();
    Sale getById(Long id);
    List<Sale> getByPais(String pais);
    List<Sale> getByFecha(LocalDate inicio, LocalDate fin);
    Double getTotalVentasEnEuros();
}

