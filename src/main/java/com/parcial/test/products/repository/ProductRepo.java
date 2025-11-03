package com.parcial.test.products.repository;

import com.parcial.test.products.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepo extends JpaRepository<Product,Long> {
}
