package com.parcial.test.sales.repository;

import com.parcial.test.sales.entities.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

public interface SalesRepo extends JpaRepository<Sale,Long> {
    List<Sale> findByPaisFilial(String paisFilial);
    List<Sale> findByFechaVentaBetween(Date inicio, Date fin);

    @Query("SELECT DISTINCT s FROM Sale s " +
           "LEFT JOIN FETCH s.cliente " +
           "LEFT JOIN FETCH s.sales sd " +
           "LEFT JOIN FETCH sd.producto")
    List<Sale> findAllWithDetails();
}
