package com.parcial.test.config;

import com.parcial.test.products.entities.MonedaOrigen;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class CurrencyConversionService {
  private final GlobalConfig globalConfig;

  public Double convertirAMonedaCorporativa(Double monto, MonedaOrigen monedaOrigen){
    return globalConfig.convertir(monto,monedaOrigen);
  }

  public String getMondaCorporativa(){
    return globalConfig.getMonedaCorporativa();
  }

}
