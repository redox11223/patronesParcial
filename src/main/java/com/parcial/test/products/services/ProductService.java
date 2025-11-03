package com.parcial.test.products.services;

import com.parcial.test.products.entities.Product;

import java.math.BigDecimal;
import java.util.List;

public interface ProductService {
  Product save(Product product);
  List<Product> getAll();
  Product update(Long id, Product product);
  Product getById(Long id);
  void delete(Long id);

}
