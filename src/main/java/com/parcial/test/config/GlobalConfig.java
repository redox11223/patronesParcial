package com.parcial.test.config;

import com.parcial.test.products.entities.MonedaOrigen;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@ConfigurationProperties(prefix = "serf.config")
@Configuration
@Data
public class GlobalConfig {
    private String monedaCorporativa="EUR";
    private Map<String, Double> tasaCambio= new HashMap<>();
    private FormatoConfig formatoConfig=new FormatoConfig();

    @PostConstruct
    public void init(){
      tasaCambio.put("CNY_EUR", 0.13);
      tasaCambio.put("USD_EUR", 0.87);
      tasaCambio.put("PEN_EUR", 0.26);
    }

    public Double convertir(Double monto, MonedaOrigen monedaOrigen){
      if(monedaOrigen.name().equals(monedaCorporativa)){
        return monto;
      }

      String tipoCambio=monedaOrigen.name()+"_"+monedaCorporativa;
      Double cambio= tasaCambio.get(tipoCambio);

      if(cambio==null){
        throw new IllegalArgumentException("No existe tasa de cambio en el sistema para:"+ monedaOrigen);
      }

      return Math.round(monto*cambio*100.0)/100.0;
    }


    @Data
    public static class FormatoConfig{
      private String formatoFecha="dd-MM-yyyy";
      private String formatoHora="HH-mm-ss";
    }
}
