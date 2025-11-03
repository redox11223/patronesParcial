# Sistema SERF - Sistema Empresarial de Gestión de Reportes Financieros

## 📋 Descripción
Sistema empresarial para FinanCorp S.A. que integra gestión de productos, ventas y generación automática de reportes financieros consolidados con conversión de monedas.

## 🎯 Patrones de Diseño Implementados

### 1. **Singleton** - Configuración Global
- **Clase**: `GlobalConfig` + `CurrencyConversionService`
- **Propósito**: Gestionar tasas de cambio y configuración corporativa de forma centralizada
- **Ubicación**: `config/GlobalConfig.java`

### 2. **Prototype** - Plantillas de Reportes
- **Clase**: `Report` (implementa `Prototype`) + `PrototypesRegistry`
- **Propósito**: Clonar plantillas predefinidas (Mensual, Trimestral, Anual)
- **Ubicación**: `reports/entities/`

### 3. **Builder** - Construcción de Reportes
- **Clase**: `ReporteBuilder` + `ReporteBuilderImpl`
- **Propósito**: Construir reportes paso a paso con múltiples secciones
- **Ubicación**: `reports/builder/`

### 4. **Composite** - Estructura Jerárquica
- **Clases**: `ComponenteReporte`, `SeccionReporte`, `SubseccionReporte`
- **Propósito**: Organizar reportes en secciones y subsecciones anidadas
- **Ubicación**: `reports/composite/`

### 5. **Decorator** - Seguridad Documental
- **Clases**: `ReporteDecorator`, `MarcaAguaDecorator`, `FirmaDigitalDecorator`
- **Propósito**: Añadir marca de agua y firma digital SHA-256
- **Ubicación**: `reports/decorator/`

### 6. **Facade** - Simplificación
- **Clase**: `ReporteService`
- **Propósito**: Ocultar toda la complejidad de generación de reportes en un solo método
- **Ubicación**: `reports/services/ReporteService.java`

---

## 🚀 Endpoints API

### **Productos**
```
POST   /v1/productos                  - Crear producto
GET    /v1/productos                  - Listar todos
GET    /v1/productos/{id}             - Obtener por ID
PUT    /v1/productos/{id}             - Actualizar
DELETE /v1/productos/{id}             - Eliminar
```

### **Clientes**
```
POST   /v1/clientes                   - Crear cliente
GET    /v1/clientes                   - Listar todos
GET    /v1/clientes/{id}              - Obtener por ID
GET    /v1/clientes/pais/{pais}       - Listar por país
```

### **Ventas**
```
POST   /v1/ventas                     - Registrar venta
GET    /v1/ventas                     - Listar todas
GET    /v1/ventas/{id}                - Obtener por ID
GET    /v1/ventas/pais/{pais}         - Listar por país
GET    /v1/ventas/fecha?inicio=...&fin=... - Listar por rango
GET    /v1/ventas/total-euros         - Total consolidado en EUR
```

### **Reportes** (🎯 **FACADE**)
```
GET    /v1/reportes/mensual?mes=1&anio=2025
GET    /v1/reportes/trimestral?trimestre=1&anio=2025
GET    /v1/reportes/anual?anio=2025
```

---

## 📝 Ejemplos de Uso

### 1. Registrar Producto Importado desde China

**Request:**
```json
POST http://localhost:8080/v1/productos
Content-Type: application/json

{
  "codigo": "LAP-001",
  "nombre": "Laptop HP ProBook 450",
  "descripcion": "Laptop empresarial Intel i5, 8GB RAM, 256GB SSD",
  "categoriaProducto": "LAPTOP",
  "monedaOrigen": "CNY",
  "costoImportacionOrigen": 5000.00,
  "stock": 500,
  "proveedor": "Shenzhen Tech Ltd"
}
```

**Response:**
```json
{
  "id": 1,
  "codigo": "LAP-001",
  "nombre": "Laptop HP ProBook 450",
  "descripcion": "Laptop empresarial Intel i5, 8GB RAM, 256GB SSD",
  "categoriaProducto": "LAPTOP",
  "monedaOrigen": "CNY",
  "costoImportacionOrigen": 5000.00,
  "costoImportacionCorp": 650.00,  // ✅ Conversión automática CNY → EUR
  "stock": 500,
  "proveedor": "Shenzhen Tech Ltd",
  "fechaImportacion": "2025-01-03T10:30:00"
}
```

### 2. Registrar Cliente

**Request:**
```json
POST http://localhost:8080/v1/clientes
Content-Type: application/json

{
  "nombre": "Corporación TechPeru S.A.",
  "documento": "20123456789",
  "telefono": "+51-987654321",
  "pais": "PERU"
}
```

### 3. Registrar Venta (Filial de Perú)

**Request:**
```json
POST http://localhost:8080/v1/ventas
Content-Type: application/json

{
  "numeroFactura": "F001-00001",
  "cliente": {
    "id": 1
  },
  "sales": [
    {
      "producto": {"id": 1},
      "cantidad": 100,
      "precioUnitario": 2500.00
    }
  ],
  "metodoPago": "TRANSFERENCIA",
  "monedaLocal": "PEN",
  "vendedorResponsable": "Juan Pérez",
  "paisFilial": "PERU"
}
```

**Resultado:**
- ✅ Stock actualizado automáticamente: 500 → 400
- ✅ Total calculado: 250,000 PEN
- ✅ Conversión automática a EUR para reportes corporativos

### 4. Generar Reporte Mensual (🎯 **FACADE**)

**Request:**
```
GET http://localhost:8080/v1/reportes/mensual?mes=1&anio=2025
```

**Response:**
```
╔════════════════════════════════════════════════════════════════════╗
║                    *** CONFIDENCIAL ***                            ║
║              FinanCorp S.A. - Documento Interno                    ║
║           Prohibida su distribución no autorizada                  ║
╚════════════════════════════════════════════════════════════════════╝

═══════════════════════════════════════════════════════════════
Reporte Mensual de Ingresos - FinanCorp S.A.
Período: 01-2025
═══════════════════════════════════════════════════════════════

╔═══════════════════════════════════════════════════════════╗
  REPORTE CONSOLIDADO FINANCIERO
╚═══════════════════════════════════════════════════════════╝

╔═══════════════════════════════════════════════════════════╗
  RESUMEN EJECUTIVO
╚═══════════════════════════════════════════════════════════╝

  ▶ Período Analizado
  ────────────────────────────────────────────────────────────
    01/01/2025 - 31/01/2025

  ▶ Total de Transacciones
  ────────────────────────────────────────────────────────────
    1 ventas registradas

  ▶ Ingresos Totales (EUR)
  ────────────────────────────────────────────────────────────
    € 65000.00

╔═══════════════════════════════════════════════════════════╗
  INGRESOS POR PAÍS/FILIAL
╚═══════════════════════════════════════════════════════════╝

  ▶ PERU
  ────────────────────────────────────────────────────────────
    Total: € 65000.00 | Porcentaje: 100.0%

═══ CONCLUSIONES ═══
1. El período analizado registró un total de 1 transacciones.

2. Los ingresos consolidados alcanzaron € 65000.00 (EUR - Moneda Corporativa).

3. La filial con mayor actividad fue: PERU.

4. El sistema SERF garantiza la consolidación automática de datos
   en moneda corporativa (EUR) para facilitar la toma de decisiones.

5. Todos los reportes generados incluyen firma digital y marca de agua
   para garantizar la autenticidad e integridad del documento.

═══════════════════════════════════════════════════════════════
Documento generado automáticamente por SERF
═══════════════════════════════════════════════════════════════

╔════════════════════════════════════════════════════════════════════╗
║                      FIRMA DIGITAL                                 ║
╠════════════════════════════════════════════════════════════════════╣
║  Algoritmo: SHA-256                                                ║
║  Hash: A7B3C9D2E1F4G5H6I7J8K9L0M1N2O3P4Q5R6S7T8U9V0W1X2Y3Z4A5B6  ║
║  Fecha de firma: 03-01-2025 14:30:45                               ║
║  Firmante: Sistema SERF - FinanCorp S.A.                          ║
║  Estado: VÁLIDO ✓                                                  ║
╚════════════════════════════════════════════════════════════════════╝

Este documento ha sido firmado digitalmente y cualquier modificación
posterior invalidará la firma.
```

---

## 🔄 Flujo de Trabajo Completo

### **Escenario Real: Importación y Venta**

1. **Compras registra producto importado** (China → EUR)
   ```
   POST /v1/productos
   - Costo: 5000 CNY
   - Sistema convierte automáticamente: 650 EUR (Singleton)
   ```

2. **Filial Perú realiza venta** (PEN → EUR)
   ```
   POST /v1/ventas
   - Precio: 250,000 PEN
   - Stock actualizado automáticamente: 500 → 400
   - Conversión para reportes: 65,000 EUR (Singleton)
   ```

3. **Gerencia solicita reporte mensual** (Facade)
   ```
   GET /v1/reportes/mensual?mes=1&anio=2025
   ```
   
   **Internamente el sistema ejecuta:**
   - ✅ **Prototype**: Clona plantilla mensual
   - ✅ **Builder**: Construye reporte paso a paso
   - ✅ **Composite**: Organiza secciones jerárquicamente
   - ✅ **Decorator**: Añade marca de agua + firma digital SHA-256
   - ✅ **Singleton**: Obtiene tasas de cambio y formato
   - ✅ **Facade**: Expone todo en un solo método simple

---

## 📊 Conversiones de Moneda (Singleton)

| Moneda | Código | Tasa → EUR |
|--------|--------|------------|
| Yuan Chino | CNY | 0.13 |
| Dólar USA | USD | 0.87 |
| Sol Peruano | PEN | 0.26 |
| Euro | EUR | 1.00 |

**Ejemplo:**
- 5000 CNY × 0.13 = **650 EUR**
- 250,000 PEN × 0.26 = **65,000 EUR**

---

## 🏗️ Estructura del Proyecto

```
src/main/java/com/parcial/test/
├── TestApplication.java
├── clients/
│   ├── entities/Client.java
│   ├── services/ClientService.java
│   ├── services/ClientServiceImpl.java
│   ├── controllers/ClientController.java
│   └── ClienteRepo.java
├── products/
│   ├── entities/Product.java
│   ├── entities/CategoriaProducto.java (enum)
│   ├── entities/MonedaOrigen.java (enum)
│   ├── services/ProductService.java
│   ├── services/ProductServiceImpl.java
│   ├── controllers/ProductController.java
│   └── repository/ProductRepo.java
├── sales/
│   ├── entities/Sale.java
│   ├── entities/SaleDetail.java
│   ├── entities/MetodoPago.java (enum)
│   ├── services/SaleService.java
│   ├── services/SaleServiceImpl.java
│   ├── controllers/SaleController.java
│   └── repository/SalesRepo.java
├── reports/
│   ├── entities/
│   │   ├── Report.java (Prototype)
│   │   ├── Prototype.java (interface)
│   │   └── PrototypesRegistry.java (Prototype Registry)
│   ├── builder/
│   │   ├── ReporteBuilder.java
│   │   ├── ReporteBuilderImpl.java
│   │   └── ReporteDTO.java
│   ├── composite/
│   │   ├── ComponenteReporte.java
│   │   ├── SeccionReporte.java
│   │   └── SubseccionReporte.java
│   ├── decorator/
│   │   ├── ReporteDecorator.java
│   │   ├── ReporteBase.java
│   │   ├── MarcaAguaDecorator.java
│   │   └── FirmaDigitalDecorator.java
│   ├── services/
│   │   └── ReporteService.java (FACADE)
│   └── controllers/
│       └── ReporteController.java
└── config/
    ├── GlobalConfig.java (SINGLETON)
    └── CurrencyConversionService.java (SINGLETON)
```

---

## ✅ Beneficios Implementados

✅ **Integración total** de inventarios, ventas y reportes
✅ **Conversión automática** de monedas (Singleton)
✅ **Actualización automática** de stock en ventas
✅ **Seguridad documental** (Marca de agua + Firma SHA-256)
✅ **Plantillas reutilizables** (Prototype)
✅ **Construcción flexible** de reportes (Builder)
✅ **Estructura jerárquica** (Composite)
✅ **Decoración dinámica** (Decorator)
✅ **Interfaz simplificada** (Facade)

---

## 🔧 Configuración

### application.properties
```properties
# Base de datos
spring.datasource.url=jdbc:postgresql://localhost:5432/serf_db
spring.datasource.username=postgres
spring.datasource.password=password
spring.jpa.hibernate.ddl-auto=update

# Configuración SERF
serf.config.moneda-corporativa=EUR
```

---

## 🧪 Pruebas Recomendadas

1. **Crear 5 productos** de diferentes categorías
2. **Crear 3 clientes** de diferentes países
3. **Registrar 10 ventas** en distintas fechas y países
4. **Generar reporte mensual** y verificar:
   - Marca de agua presente
   - Firma digital SHA-256
   - Conversión correcta a EUR
   - Secciones jerárquicas (Composite)

---

## 👨‍💻 Autor
**Sistema SERF - FinanCorp S.A.**
Implementado con Spring Boot + JPA + Lombok
Patrones de diseño: Singleton, Prototype, Builder, Composite, Decorator, Facade

