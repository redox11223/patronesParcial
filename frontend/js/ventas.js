let productos = [];
let clientes = [];
let ventas = [];

async function cargarDatos() {
    try {
        productos = await api.get('/productos');
        clientes = await api.get('/clientes');

        llenarSelectClientes();
        llenarSelectProductos();

        // Cargar ventas
        try {
            ventas = await api.get('/ventas');
            console.log('Ventas cargadas:', ventas);
            mostrarVentas();
        } catch (ventasError) {
            console.error('Error detallado al cargar ventas:', ventasError);
            document.getElementById('ventasTable').innerHTML =
                `<p class="loading" style="color: red;">⚠️ Error al cargar ventas: ${ventasError.message}</p>`;
        }
    } catch (error) {
        alert('Error al cargar datos: ' + error.message);
    }
}

function llenarSelectClientes() {
    const select = document.getElementById('clienteId');
    select.innerHTML = '<option value="">Seleccione un cliente...</option>';

    clientes.forEach(cliente => {
        const option = document.createElement('option');
        option.value = cliente.id;
        option.textContent = `${cliente.nombre} - ${cliente.documento}`;
        select.appendChild(option);
    });
}

function llenarSelectProductos() {
    const select = document.getElementById('productoId');
    select.innerHTML = '<option value="">Seleccione un producto...</option>';

    productos.forEach(producto => {
        const option = document.createElement('option');
        option.value = producto.id;
        const precio = producto.costoImportacionCorp || producto.costoImportacionOrigen || 0;
        option.textContent = `${producto.nombre} - ¥ ${precio.toFixed(2)}`;
        option.dataset.precio = precio;
        select.appendChild(option);
    });
}

function actualizarPrecio() {
    const select = document.getElementById('productoId');
    const selectedOption = select.options[select.selectedIndex];

    if (selectedOption.dataset.precio) {
        document.getElementById('precioUnitario').value = `$${parseFloat(selectedOption.dataset.precio).toFixed(2)}`;
        calcularTotal();
    } else {
        document.getElementById('precioUnitario').value = '';
        document.getElementById('montoTotal').value = '';
    }
}

function calcularTotal() {
    const cantidad = parseInt(document.getElementById('cantidad').value) || 0;
    const select = document.getElementById('productoId');
    const selectedOption = select.options[select.selectedIndex];

    if (selectedOption.dataset.precio && cantidad > 0) {
        const precio = parseFloat(selectedOption.dataset.precio);
        const total = precio * cantidad;
        document.getElementById('montoTotal').value = `$${total.toFixed(2)}`;
    } else {
        document.getElementById('montoTotal').value = '';
    }
}

async function guardarVenta(event) {
    event.preventDefault();

    const venta = {
        clienteId: parseInt(document.getElementById('clienteId').value),
        productoId: parseInt(document.getElementById('productoId').value),
        cantidad: parseInt(document.getElementById('cantidad').value)
    };

    try {
        await api.post('/ventas/simple', venta);
        alert('Venta registrada exitosamente');
        document.getElementById('ventaForm').reset();
        document.getElementById('precioUnitario').value = '';
        document.getElementById('montoTotal').value = '';
        cargarDatos();
    } catch (error) {
        alert('Error al guardar venta: ' + error.message);
    }
}

function mostrarVentas() {
    const container = document.getElementById('ventasTable');

    if (ventas.length === 0) {
        container.innerHTML = '<p class="loading">No hay ventas registradas</p>';
        return;
    }

    let html = `
        <table>
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Cliente</th>
                    <th>Producto</th>
                    <th>Cantidad</th>
                    <th>Monto Total (USD)</th>
                    <th>Fecha</th>
                </tr>
            </thead>
            <tbody>
    `;

    ventas.forEach(venta => {
        const cliente = clientes.find(c => c.id === venta.clienteId);

        // Usar productoNombre si está disponible, sino buscar en la lista
        let productoNombre = venta.productoNombre;
        if (!productoNombre && venta.productoId) {
            const producto = productos.find(p => p.id === venta.productoId);
            productoNombre = producto ? producto.nombre : 'N/A';
        }

        html += `
            <tr>
                <td>${venta.id}</td>
                <td>${cliente ? cliente.nombre : 'N/A'}</td>
                <td>${productoNombre || 'N/A'}</td>
                <td>${venta.cantidad !== undefined && venta.cantidad !== null ? venta.cantidad : 'N/A'}</td>
                <td>$${venta.montoTotal ? venta.montoTotal.toFixed(2) : '0.00'}</td>
                <td>${venta.fechaVenta ? new Date(venta.fechaVenta).toLocaleDateString() : 'N/A'}</td>
            </tr>
        `;
    });

    html += `
            </tbody>
        </table>
    `;

    container.innerHTML = html;
}

document.addEventListener('DOMContentLoaded', cargarDatos);
