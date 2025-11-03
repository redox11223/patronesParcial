package com.parcial.test.products.controllers;

import com.parcial.test.products.entities.Product;
import com.parcial.test.products.services.ProductServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/productos")
@RequiredArgsConstructor
public class ProductController {
  private final ProductServiceImpl productService;

  @GetMapping
  public ResponseEntity<List<Product>> getAllProducts(){
    List<Product> products= productService.getAll();
    return ResponseEntity.ok(products);
  }

  @GetMapping("/{id}")
  public ResponseEntity<Product> getProduct(@PathVariable Long id){
    Product product=productService.getById(id);
    return ResponseEntity.ok(product);
  }

  @PostMapping
  public ResponseEntity<Product> saveProduct(@RequestBody Product product){
    Product newProduct=productService.save(product);
    return ResponseEntity.status(HttpStatus.CREATED).body(newProduct);
  }

  @PutMapping("/{id}")
  public ResponseEntity<Product> updateProduct(@PathVariable Long id,@RequestBody Product product){
    Product updatedProduct=productService.update(id,product);
    return  ResponseEntity.ok(updatedProduct);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteProduct(@PathVariable Long id){
    productService.delete(id);
    return  ResponseEntity.noContent().build();
  }

}
