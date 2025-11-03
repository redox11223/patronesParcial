package com.parcial.test.sales.repository;

import com.parcial.test.sales.entities.Sale;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface SalesRepo extends JpaRepository<Sale,Long> {
    List<Sale> findByPaisFilial(String paisFilial);
    List<Sale> findByFechaVentaBetween(Date inicio, Date fin);
}
