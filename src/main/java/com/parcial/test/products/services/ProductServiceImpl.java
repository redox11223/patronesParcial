package com.parcial.test.products.services;

import com.parcial.test.config.CurrencyConversionService;
import com.parcial.test.products.entities.Product;
import com.parcial.test.products.repository.ProductRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

  private final ProductRepo productRepo;
  private final CurrencyConversionService currencyConversionService;

  @Override
  public Product save(Product product) {
    Double montoCorporativo= currencyConversionService.convertirAMonedaCorporativa(product.getCostoImportacionOrigen(), product.getMonedaOrigen());
    product.setCostoImportacionCorp(montoCorporativo);
    return productRepo.save(product);
  }

  @Override
  public List<Product> getAll() {
    return productRepo.findAll();
  }

  @Override
  public Product update(Long id, Product product) {
    Product updatedProduct=getById(id);
    updatedProduct.setNombre(product.getNombre());
    updatedProduct.setDescripcion(product.getDescripcion());
    updatedProduct.setCodigo(product.getCodigo());
    updatedProduct.setMonedaOrigen(product.getMonedaOrigen());
    updatedProduct.setCategoriaProducto(product.getCategoriaProducto());
    updatedProduct.setCostoImportacionOrigen(product.getCostoImportacionOrigen());
    updatedProduct.setCostoImportacionCorp(product.getCostoImportacionCorp());
    updatedProduct.setStock(product.getStock());
    return productRepo.save(updatedProduct);
  }

  @Override
  public Product getById(Long id) {
    return productRepo.findById(id).orElse(null);
  }

  @Override
  public void delete(Long id) {
    productRepo.deleteById(id);
  }
}
