package com.parcial.test.sales.repository;

import com.parcial.test.sales.entities.SaleDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SalesDetailsRepo extends JpaRepository<SaleDetail,Long> {
}
