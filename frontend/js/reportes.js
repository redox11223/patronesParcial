async function generarReporte(event) {
    event.preventDefault();

    const tipoReporte = document.getElementById('tipoReporte').value;
    const conMarcaAgua = document.getElementById('conMarcaAgua').checked;
    const conFirmaDigital = document.getElementById('conFirmaDigital').checked;

    try {
        console.log('Solicitando reporte:', tipoReporte);

        let url = `/reportes/${tipoReporte}`;
        let datos;

        // Agregar parámetros para reportes trimestrales y anuales
        if (tipoReporte === 'reporte-trimestral') {
            const trimestre = document.getElementById('trimestre').value;
            const anio = document.getElementById('anioTrimestre').value;
            url += `?trimestre=${trimestre}&anio=${anio}`;
            // Usar getText para obtener texto plano
            datos = await api.getText(url);
        } else if (tipoReporte === 'reporte-anual') {
            const anio = document.getElementById('anioAnual').value;
            url += `?anio=${anio}`;
            // Usar getText para obtener texto plano
            datos = await api.getText(url);
        } else {
            // Usar get normal para JSON
            datos = await api.get(url);
        }

        console.log('Datos recibidos:', datos);

        if (!datos) {
            alert('No se recibieron datos del servidor');
            return;
        }

        let reporte;

        // Si el reporte ya viene formateado como texto (trimestral y anual)
        if (tipoReporte === 'reporte-trimestral' || tipoReporte === 'reporte-anual') {
            reporte = datos;
        } else {
            reporte = generarContenidoReporte(tipoReporte, datos);
        }

        console.log('Reporte generado, longitud:', reporte.length);

        if (conMarcaAgua && (tipoReporte !== 'reporte-trimestral' && tipoReporte !== 'reporte-anual')) {
            reporte = agregarMarcaAgua(reporte);
        }

        if (conFirmaDigital && (tipoReporte !== 'reporte-trimestral' && tipoReporte !== 'reporte-anual')) {
            reporte = agregarFirmaDigital(reporte);
        }

        mostrarReporte(reporte);
    } catch (error) {
        console.error('Error completo:', error);
        alert('Error al generar reporte: ' + error.message);
    }
}

// Función para mostrar/ocultar campos de parámetros según el tipo de reporte
function mostrarParametrosReporte() {
    const tipoReporte = document.getElementById('tipoReporte').value;
    const parametrosTrimestre = document.getElementById('parametrosTrimestre');
    const parametrosAnual = document.getElementById('parametrosAnual');

    // Ocultar todos los parámetros
    parametrosTrimestre.style.display = 'none';
    parametrosAnual.style.display = 'none';

    // Mostrar parámetros según el tipo
    if (tipoReporte === 'reporte-trimestral') {
        parametrosTrimestre.style.display = 'block';
    } else if (tipoReporte === 'reporte-anual') {
        parametrosAnual.style.display = 'block';
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

// Función para exportar el reporte a PDF
function exportarAPDF() {
    const { jsPDF } = window.jspdf;
    const contenido = document.getElementById('reporteContenido').textContent;

    if (!contenido) {
        alert('No hay ningún reporte generado para exportar');
        return;
    }

    try {
        // Crear instancia de jsPDF
        const doc = new jsPDF({
            orientation: 'portrait',
            unit: 'mm',
            format: 'a4'
        });

        // Configurar fuente monoespaciada para mantener el formato
        doc.setFont('courier');
        doc.setFontSize(8);

        // Dividir el contenido en líneas
        const lineas = contenido.split('\n');

        // Configuración de márgenes y posición
        const margenIzquierdo = 10;
        const margenSuperior = 10;
        const alturaLinea = 4;
        const lineaPorPagina = 65;
        let y = margenSuperior;
        let numeroPagina = 1;

        // Agregar líneas al PDF
        lineas.forEach((linea, index) => {
            // Si llegamos al final de la página, crear nueva página
            if (index > 0 && index % lineaPorPagina === 0) {
                doc.addPage();
                y = margenSuperior;
                numeroPagina++;
            }

            // Agregar la línea al PDF
            doc.text(linea, margenIzquierdo, y);
            y += alturaLinea;
        });

        // Agregar número de páginas en el pie de página
        const totalPaginas = doc.internal.getNumberOfPages();
        for (let i = 1; i <= totalPaginas; i++) {
            doc.setPage(i);
            doc.setFontSize(8);
            doc.text(
                `Página ${i} de ${totalPaginas}`,
                doc.internal.pageSize.getWidth() / 2,
                doc.internal.pageSize.getHeight() - 10,
                { align: 'center' }
            );
        }

        // Generar nombre de archivo con fecha
        const tipoReporte = document.getElementById('tipoReporte').selectedOptions[0].text;
        const fecha = new Date().toISOString().split('T')[0];
        const nombreArchivo = `Reporte_${tipoReporte.replace(/\s+/g, '_')}_${fecha}.pdf`;

        // Descargar el PDF
        doc.save(nombreArchivo);

        console.log('PDF generado exitosamente:', nombreArchivo);
        alert('✅ Reporte exportado a PDF exitosamente');

    } catch (error) {
        console.error('Error al generar PDF:', error);
        alert('❌ Error al exportar a PDF: ' + error.message);
    }
}

