# SERF - Sistema Empresarial de Reportes Financieros

Aplicación backend desarrollada para FinanCorp S.A. que gestiona productos importados, ventas internacionales y generación automática de reportes financieros consolidados con conversión de divisas.

## Tecnologías

- **Java 17** con Spring Boot 3.x
- **Spring Data JPA** para persistencia
- **H2 Database** (desarrollo) / Compatible con PostgreSQL
- **Lombok** para reducir boilerplate
- **Maven** como gestor de dependencias
- **Swagger/OpenAPI** para documentación de API

## Arquitectura del Sistema

El proyecto está organizado en módulos independientes por dominio:

```
src/main/java/com/parcial/test/
├── clients/           # Gestión de clientes
├── products/          # Catálogo de productos
├── sales/             # Procesamiento de ventas
├── reports/           # Generación de reportes
├── config/            # Configuración global
└── exceptions/        # Manejo centralizado de errores
```

## Patrones de Diseño

El sistema implementa varios patrones GoF para mantener el código limpio y escalable:

### Singleton
La configuración global (`GlobalConfig`) se gestiona como singleton, manteniendo las tasas de cambio de monedas centralizadas. Esto evita inconsistencias cuando múltiples partes del sistema necesitan realizar conversiones.

### Prototype
Las plantillas de reportes se clonan en lugar de crearse desde cero. El `PrototypesRegistry` mantiene tres plantillas base (mensual, trimestral, anual) que se pueden duplicar y personalizar según necesidad.

### Builder
Construir un reporte completo puede involucrar muchos pasos: título, período, secciones, subsecciones, gráficos, conclusiones. El `ReporteBuilder` permite ir agregando estos elementos de forma fluida sin constructores complejos.

### Composite
Los reportes tienen estructura jerárquica (secciones que contienen subsecciones). El patrón Composite permite tratarlos uniformemente: tanto `SeccionReporte` como `SubseccionReporte` implementan `ComponenteReporte`.

### Decorator
Para agregar seguridad a los reportes (marcas de agua, firmas digitales) sin modificar las clases originales, se usan decoradores que envuelven el contenido y añaden las capas necesarias.

### Facade
La complejidad de generar un reporte (obtener plantilla, construir estructura, agregar decoradores, persistir) se oculta tras una interfaz simple en `ReporteService`.

### Chain of Responsibility
El sistema de excepciones usa una cadena de handlers que procesan errores específicos. Si un handler no puede procesar una excepción, la pasa al siguiente en la cadena.

## API REST

### Productos
- `POST /v1/productos` - Registrar nuevo producto
- `GET /v1/productos` - Listar todos los productos
- `GET /v1/productos/{id}` - Consultar producto específico
- `PUT /v1/productos/{id}` - Actualizar datos
- `DELETE /v1/productos/{id}` - Eliminar del catálogo

### Clientes
- `POST /v1/clients` - Crear cliente
- `GET /v1/clients` - Listar clientes
- `GET /v1/clients/{id}` - Obtener cliente por ID
- `GET /v1/clients/pais/{pais}` - Filtrar por país
- `DELETE /v1/clients/{id}` - Eliminar cliente

### Ventas
- `POST /v1/sales` - Registrar venta (actualiza stock automáticamente)
- `GET /v1/sales` - Historial de ventas
- `GET /v1/sales/{id}` - Detalle de venta
- `GET /v1/sales/pais/{pais}` - Ventas por filial
- `GET /v1/sales/fecha?inicio={fecha}&fin={fecha}` - Rango de fechas
- `GET /v1/sales/total-euros` - Total consolidado en EUR

### Reportes
- `GET /v1/reportes` - Listar reportes generados
- `GET /v1/reportes/{id}` - Obtener reporte específico
- `POST /v1/reportes/generar` - Crear reporte personalizado
- `GET /v1/reportes/plantillas` - Ver plantillas disponibles

## Modelo de Datos

### Product
```java
{
  "codigo": "LAPTOP-001",
  "nombre": "MacBook Pro 14",
  "categoriaProducto": "LAPTOP",
  "monedaOrigen": "USD",
  "costoImportacionOrigen": 1999.99,
  "stock": 50,
  "proveedor": "Apple Inc."
}
```

### Client
```java
{
  "nombre": "Empresa XYZ S.A.",
  "documento": "20123456789",
  "telefono": "+51999888777",
  "pais": "Peru"
}
```

### Sale
```java
{
  "numeroFactura": "FAC-2024-001",
  "clienteId": 1,
  "metodoPago": "TRANSFERENCIA",
  "monedaLocal": "PEN",
  "vendedorResponsable": "Juan Pérez",
  "paisFilial": "Peru",
  "sales": [
    {
      "productoId": 1,
      "cantidad": 2,
      "precioUnitario": 1999.99
    }
  ]
}
```

## Conversión de Monedas

El sistema maneja múltiples monedas y convierte todo a EUR (moneda corporativa):

- **PEN → EUR**: 0.26
- **USD → EUR**: 0.87
- **CNY → EUR**: 0.13

Las conversiones se aplican automáticamente al registrar productos y calcular totales de ventas.

## Validaciones

Todos los endpoints incluyen validaciones robustas:

- Campos obligatorios no pueden ser nulos o vacíos
- Costos deben ser positivos
- Stock se verifica antes de procesar ventas
- Fechas se validan (inicio < fin)
- Las conversiones de moneda fallan si no existe tasa configurada

## Gestión de Errores

Las respuestas de error siguen un formato JSON consistente:

```json
{
  "timestamp": "2025-11-04T15:30:00",
  "status": 404,
  "error": "Not Found",
  "errorCode": "RESOURCE_NOT_FOUND",
  "message": "Producto no encontrado con identificador: 123",
  "path": "/v1/productos/123"
}
```

Códigos de error implementados:
- `RESOURCE_NOT_FOUND` (404) - Recurso no existe
- `VALIDATION_ERROR` (400) - Datos inválidos
- `BUSINESS_LOGIC_ERROR` (400) - Regla de negocio violada
- `CONFIGURATION_ERROR` (500) - Error de configuración del sistema

## Instalación y Ejecución

```bash
# Clonar repositorio
git clone <repo-url>
cd patronesParcial

# Compilar proyecto
mvn clean install

# Ejecutar aplicación
mvn spring-boot:run

# La aplicación estará disponible en:
# http://localhost:8080
```

## Documentación Swagger

Una vez ejecutada la aplicación, la documentación interactiva está disponible en:

```
http://localhost:8080/swagger-ui.html
```

Desde ahí puedes probar todos los endpoints directamente desde el navegador.

## Configuración

El archivo `application.properties` contiene la configuración principal:

```properties
# Base de datos H2 (en memoria)
spring.datasource.url=jdbc:h2:mem:testdb
spring.h2.console.enabled=true

# JPA
spring.jpa.show-sql=true
spring.jpa.hibernate.ddl-auto=update

# Puerto
server.port=8080
```

## Frontend

El proyecto incluye un frontend simple en `/frontend`:

- `index.html` - Dashboard principal
- `pages/clientes.html` - Gestión de clientes
- `pages/productos.html` - Catálogo de productos
- `pages/ventas.html` - Registro de ventas
- `pages/reportes.html` - Generación de reportes

Para usarlo, simplemente abre `index.html` en un navegador después de iniciar el backend.

## Diagramas UML

El directorio raíz contiene diagramas Mermaid (.mmd) y SVG de la arquitectura:

- `diagrama_entidades.mmd` - Modelo de dominio completo
- `patron_prototype.mmd` - Implementación del patrón Prototype
- `patron_builder.mmd` - Implementación del patrón Builder
- `patron_composite.mmd` - Estructura de reportes
- `patron_decorator.mmd` - Decoradores de seguridad
- `patron_singleton.mmd` - Configuración global
- `patron_facade.mmd` - Fachada de servicios
- `patron_chain_of_responsibility.mmd` - Manejo de excepciones
- `patron_exception_hierarchy.mmd` - Jerarquía de errores

Estos se pueden visualizar en [mermaid.live](https://mermaid.live) o con plugins de IDE.

## Testing

```bash
# Ejecutar tests unitarios
mvn test

# Ejecutar tests con cobertura
mvn test jacoco:report
```

## Estructura de Paquetes

```
com.parcial.test
├── clients
│   ├── entities/          # Client.java
│   ├── controllers/       # ClientController.java
│   ├── services/          # ClientService, ClientServiceImpl
│   └── ClienteRepo.java
├── products
│   ├── entities/          # Product, CategoriaProducto, MonedaOrigen
│   ├── controllers/       # ProductController
│   ├── services/          # ProductService, ProductServiceImpl
│   └── repository/        # ProductRepo
├── sales
│   ├── entities/          # Sale, SaleDetail, MetodoPago
│   ├── controllers/       # SaleController
│   ├── services/          # SaleService, SaleServiceImpl
│   ├── repository/        # SalesRepo
│   └── dto/               # SaleDTO
├── reports
│   ├── entities/          # Report, Prototype, PrototypesRegistry
│   ├── builder/           # ReporteBuilder, ReporteBuilderImpl, ReporteDTO
│   ├── composite/         # ComponenteReporte, SeccionReporte, SubseccionReporte
│   ├── decorator/         # ReporteDecorator, FirmaDigitalDecorator, MarcaAguaDecorator
│   ├── controllers/       # ReportController
│   ├── services/          # ReportService
│   └── repository/        # ReportRepo
├── config
│   ├── GlobalConfig.java
│   ├── CurrencyConversionService.java
│   ├── CorsConfig.java
│   └── SwaggerConfig.java
└── exceptions
    ├── BaseException.java
    ├── ResourceNotFoundException.java
    ├── BusinessLogicException.java
    ├── ValidationException.java
    ├── ConfigurationException.java
    ├── ReportGenerationException.java
    ├── dto/
    │   └── ErrorResponse.java
    └── handler/
        ├── ExceptionHandler.java
        ├── AbstractExceptionHandler.java
        ├── BaseExceptionHandler.java
        ├── ValidationExceptionHandler.java
        ├── IllegalArgumentExceptionHandler.java
        └── GlobalExceptionHandler.java
```

## Contribuir

Si encuentras algún bug o tienes sugerencias:

1. Abre un issue describiendo el problema
2. Si tienes una solución, crea un pull request
3. Asegúrate de que los tests pasen antes de enviar

## Licencia

Este proyecto es privado y pertenece a FinanCorp S.A.

---

Desarrollado como parte del sistema de gestión empresarial de FinanCorp S.A.
