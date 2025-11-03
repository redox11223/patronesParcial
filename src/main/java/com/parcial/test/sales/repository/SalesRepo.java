package com.parcial.test.sales.repository;

import com.parcial.test.sales.entities.Sale;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SalesRepo extends JpaRepository<Sale,Long> {
}
