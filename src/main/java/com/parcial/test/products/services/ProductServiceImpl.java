package com.parcial.test.products.services;

import com.parcial.test.config.CurrencyConversionService;
import com.parcial.test.exceptions.ResourceNotFoundException;
import com.parcial.test.exceptions.ValidationException;
import com.parcial.test.exceptions.BusinessLogicException;
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
    validateProduct(product);

    try {
      Double montoCorporativo = currencyConversionService.convertirAMonedaCorporativa(
          product.getCostoImportacionOrigen(),
          product.getMonedaOrigen()
      );
      product.setCostoImportacionCorp(montoCorporativo);
      return productRepo.save(product);
    } catch (IllegalArgumentException e) {
      throw new BusinessLogicException("Error al convertir moneda: " + e.getMessage(), e);
    }
  }

  @Override
  public List<Product> getAll() {
    return productRepo.findAll();
  }

  @Override
  public Product update(Long id, Product product) {
    Product updatedProduct = productRepo.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Producto", String.valueOf(id)));

    validateProduct(product);

    updatedProduct.setNombre(product.getNombre());
    updatedProduct.setDescripcion(product.getDescripcion());
    updatedProduct.setCodigo(product.getCodigo());
    updatedProduct.setMonedaOrigen(product.getMonedaOrigen());
    updatedProduct.setCategoriaProducto(product.getCategoriaProducto());
    updatedProduct.setCostoImportacionOrigen(product.getCostoImportacionOrigen());
    updatedProduct.setStock(product.getStock());
    updatedProduct.setProveedor(product.getProveedor());

    try {
      // Recalcular el costo corporativo con la conversión de moneda
      Double montoCorporativo = currencyConversionService.convertirAMonedaCorporativa(
          product.getCostoImportacionOrigen(),
          product.getMonedaOrigen()
      );
      updatedProduct.setCostoImportacionCorp(montoCorporativo);
    } catch (IllegalArgumentException e) {
      throw new BusinessLogicException("Error al convertir moneda: " + e.getMessage(), e);
    }

    return productRepo.save(updatedProduct);
  }

  @Override
  public Product getById(Long id) {
    return productRepo.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Producto", String.valueOf(id)));
  }

  @Override
  public void delete(Long id) {
    if (!productRepo.existsById(id)) {
      throw new ResourceNotFoundException("Producto", String.valueOf(id));
    }
    productRepo.deleteById(id);
  }

  private void validateProduct(Product product) {
    if (product.getNombre() == null || product.getNombre().trim().isEmpty()) {
      throw new ValidationException("nombre", "El nombre es obligatorio");
    }
    if (product.getCodigo() == null || product.getCodigo().trim().isEmpty()) {
      throw new ValidationException("codigo", "El código es obligatorio");
    }
    if (product.getCategoriaProducto() == null) {
      throw new ValidationException("categoriaProducto", "La categoría es obligatoria");
    }
    if (product.getMonedaOrigen() == null) {
      throw new ValidationException("monedaOrigen", "La moneda de origen es obligatoria");
    }
    if (product.getCostoImportacionOrigen() == null || product.getCostoImportacionOrigen() <= 0) {
      throw new ValidationException("costoImportacionOrigen", "El costo debe ser mayor a 0");
    }
  }
}
