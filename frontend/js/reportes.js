async function generarReporte(event) {
    event.preventDefault();

    const tipoReporte = document.getElementById('tipoReporte').value;
    const conMarcaAgua = document.getElementById('conMarcaAgua').checked;
    const conFirmaDigital = document.getElementById('conFirmaDigital').checked;

    try {
        console.log('Solicitando reporte:', tipoReporte);
        const datos = await api.get(`/reportes/${tipoReporte}`);
        console.log('Datos recibidos:', datos);

        if (!datos) {
            alert('No se recibieron datos del servidor');
            return;
        }

        let reporte = generarContenidoReporte(tipoReporte, datos);
        console.log('Reporte generado, longitud:', reporte.length);

        if (conMarcaAgua) {
            reporte = agregarMarcaAgua(reporte);
        }

        if (conFirmaDigital) {
            reporte = agregarFirmaDigital(reporte);
        }

        mostrarReporte(reporte);
    } catch (error) {
        console.error('Error completo:', error);
        alert('Error al generar reporte: ' + error.message);
    }
}

function generarContenidoReporte(tipo, datos) {
    console.log('Generando contenido para tipo:', tipo);
    console.log('Datos:', datos);
    console.log('Es array?', Array.isArray(datos));
    console.log('Cantidad de elementos:', datos ? datos.length : 0);

    const fecha = new Date().toLocaleString();
    let contenido = '';

    contenido += '╔════════════════════════════════════════════════════════════╗\n';
    contenido += '║            FINANCORP - SISTEMA EMPRESARIAL SERF            ║\n';
    contenido += '╚════════════════════════════════════════════════════════════╝\n\n';

    switch (tipo) {
        case 'ventas-mensuales':
            contenido += `📊 REPORTE DE VENTAS MENSUALES\n`;
            contenido += `📅 Fecha de Generación: ${fecha}\n\n`;
            contenido += `─────────────────────────────────────────────────────────────\n`;

            if (Array.isArray(datos) && datos.length > 0) {
                let totalVentas = 0;
                datos.forEach((venta, index) => {
                    contenido += `Venta #${index + 1}:\n`;
                    contenido += `  Cliente: ${venta.clienteNombre || `ID: ${venta.clienteId}` || 'N/A'}\n`;
                    contenido += `  Producto: ${venta.productoNombre || 'N/A'}\n`;
                    contenido += `  Cantidad: ${venta.cantidad !== null && venta.cantidad !== undefined ? venta.cantidad : 'N/A'}\n`;
                    contenido += `  Monto: $${venta.montoTotal ? venta.montoTotal.toFixed(2) : '0.00'}\n`;
                    contenido += `  Fecha: ${venta.fechaVenta ? new Date(venta.fechaVenta).toLocaleDateString() : 'N/A'}\n\n`;
                    totalVentas += venta.montoTotal || 0;
                });
                contenido += `─────────────────────────────────────────────────────────────\n`;
                contenido += `💰 TOTAL VENTAS DEL MES: $${totalVentas.toFixed(2)}\n`;
            } else {
                contenido += 'No hay ventas registradas en el mes actual.\n';
            }
            break;

        case 'productos-stock':
            contenido += `📦 REPORTE DE PRODUCTOS EN STOCK\n`;
            contenido += `📅 Fecha de Generación: ${fecha}\n\n`;
            contenido += `─────────────────────────────────────────────────────────────\n`;

            if (Array.isArray(datos) && datos.length > 0) {
                datos.forEach(producto => {
                    contenido += `• ${producto.nombre || producto.nombreProducto || 'N/A'}\n`;
                    contenido += `  Código: ${producto.codigo || 'N/A'}\n`;
                    contenido += `  Stock: ${producto.stock !== undefined ? producto.stock : (producto.cantidadStock || 'N/A')} unidades\n`;
                    const precio = producto.costoImportacionCorp || producto.costoImportacionOrigen || producto.precioUnitario || 0;
                    contenido += `  Precio: $${precio.toFixed(2)}\n`;
                    contenido += `  Categoría: ${producto.categoriaProducto || 'N/A'}\n`;
                    contenido += `  Proveedor: ${producto.proveedor || 'N/A'}\n\n`;
                });
            } else {
                contenido += 'No hay productos en el inventario.\n';
            }
            break;

        case 'clientes-activos':
            contenido += `👥 REPORTE DE CLIENTES ACTIVOS\n`;
            contenido += `📅 Fecha de Generación: ${fecha}\n\n`;
            contenido += `─────────────────────────────────────────────────────────────\n`;

            if (Array.isArray(datos) && datos.length > 0) {
                datos.forEach((cliente, index) => {
                    contenido += `${index + 1}. ${cliente.nombre || cliente.nombreCliente || 'N/A'}\n`;
                    contenido += `   📧 Email: ${cliente.email || cliente.emailCliente || 'N/A'}\n`;
                    contenido += `   📞 Teléfono: ${cliente.telefono || cliente.telefonoCliente || 'N/A'}\n`;
                    contenido += `   📍 País: ${cliente.pais || 'N/A'}\n\n`;
                });
                contenido += `─────────────────────────────────────────────────────────────\n`;
                contenido += `📊 TOTAL DE CLIENTES ACTIVOS: ${datos.length}\n`;
            } else {
                contenido += 'No hay clientes registrados.\n';
            }
            break;
    }

    contenido += '\n═══════════════════════════════════════════════════════════\n';
    contenido += '              Fin del Reporte - FinanCorp SERF              \n';
    contenido += '═══════════════════════════════════════════════════════════\n';

    return contenido;
}

function agregarMarcaAgua(contenido) {
    const marcaAgua = '\n🔒 [CONFIDENCIAL - SOLO USO INTERNO]\n';
    const lineas = contenido.split('\n');
    const medio = Math.floor(lineas.length / 2);
    lineas.splice(medio, 0, marcaAgua);
    return lineas.join('\n');
}

function agregarFirmaDigital(contenido) {
    const firma = `
═══════════════════════════════════════════════════════════
                      FIRMA DIGITAL
───────────────────────────────────────────────────────────
Generado por: Sistema SERF - FinanCorp
Hash: ${generarHashSimulado()}
Fecha: ${new Date().toISOString()}
═══════════════════════════════════════════════════════════
`;
    return contenido + firma;
}

function generarHashSimulado() {
    return 'SHA256:' + Math.random().toString(36).substring(2, 15) +
           Math.random().toString(36).substring(2, 15).toUpperCase();
}

function mostrarReporte(contenido) {
    console.log('Mostrando reporte...');
    const reporteContenidoElement = document.getElementById('reporteContenido');
    const reporteResultadoElement = document.getElementById('reporteResultado');

    if (!reporteContenidoElement || !reporteResultadoElement) {
        console.error('Elementos del DOM no encontrados');
        alert('Error: No se encontraron los elementos para mostrar el reporte');
        return;
    }

    reporteContenidoElement.textContent = contenido;
    reporteResultadoElement.style.display = 'block';

    console.log('Reporte mostrado exitosamente');

    // Scroll suave al reporte
    setTimeout(() => {
        reporteResultadoElement.scrollIntoView({
            behavior: 'smooth',
            block: 'start'
        });
    }, 100);
}

// Función de prueba para verificar que el display funciona
function probarReporte() {
    console.log('Ejecutando prueba de reporte...');
    const reportePrueba = `
╔════════════════════════════════════════════════════════════╗
║            FINANCORP - SISTEMA EMPRESARIAL SERF            ║
╚════════════════════════════════════════════════════════════╝

📊 REPORTE DE PRUEBA
📅 Fecha de Generación: ${new Date().toLocaleString()}

─────────────────────────────────────────────────────────────

Este es un reporte de prueba para verificar que el sistema
de visualización funciona correctamente.

✅ Si puedes ver este mensaje, el sistema está funcionando.

─────────────────────────────────────────────────────────────

═══════════════════════════════════════════════════════════
              Fin del Reporte - FinanCorp SERF
═══════════════════════════════════════════════════════════
`;
    mostrarReporte(reportePrueba);
}
