let productos = [];

async function cargarProductos() {
    try {
        productos = await api.get('/productos');
        mostrarProductos();
    } catch (error) {
        alert('Error al cargar productos: ' + error.message);
    }
}

function mostrarProductos() {
    const container = document.getElementById('productosTable');

    if (productos.length === 0) {
        container.innerHTML = '<p class="loading">No hay productos registrados</p>';
        return;
    }

    let html = `
        <table>
            <thead>
                <tr>
                    <th>ID</th>
                    <th>CÓDIGO</th>
                    <th>NOMBRE</th>
                    <th>STOCK</th>
                    <th>PRECIO</th>
                    <th>CATEGORÍA</th>
                    <th>PROVEEDOR</th>
                    <th>ACCIONES</th>
                </tr>
            </thead>
            <tbody>
    `;

    productos.forEach(producto => {
        html += `
            <tr>
                <td>${producto.id}</td>
                <td>${producto.codigo}</td>
                <td>${producto.nombre}</td>
                <td>${producto.stock}</td>
                <td>€${producto.costoImportacionCorp ? producto.costoImportacionCorp.toFixed(2) : '0.00'}</td>
                <td>${producto.categoriaProducto}</td>
                <td>${producto.proveedor || 'N/A'}</td>
                <td>
                    <button onclick="editarProducto(${producto.id})" style="background: #ffc107; color: white;">✏️ Editar</button>
                    <button onclick="eliminarProducto(${producto.id})" style="background: #dc3545; color: white;">🗑️ Eliminar</button>
                </td>
            </tr>
        `;
    });

    html += `
            </tbody>
        </table>
    `;

    container.innerHTML = html;
}

function mostrarFormularioNuevo() {
    document.getElementById('formularioProducto').style.display = 'block';
    document.getElementById('tituloFormulario').textContent = 'Nuevo Producto';
    document.getElementById('productoForm').reset();
    document.getElementById('productoId').value = '';
}

function cancelarFormulario() {
    document.getElementById('formularioProducto').style.display = 'none';
    document.getElementById('productoForm').reset();
}

async function guardarProducto(event) {
    event.preventDefault();

    const producto = {
        codigo: document.getElementById('codigoProducto').value,
        nombre: document.getElementById('nombreProducto').value,
        descripcion: document.getElementById('descripcionProducto').value || '',
        stock: parseInt(document.getElementById('cantidadStock').value),
        costoImportacionOrigen: parseFloat(document.getElementById('precioUnitario').value),
        categoriaProducto: document.getElementById('categoriaProducto').value,
        monedaOrigen: document.getElementById('monedaOrigen').value,
        proveedor: document.getElementById('proveedor').value
    };

    try {
        const id = document.getElementById('productoId').value;
        if (id) {
            await api.put(`/productos/${id}`, producto);
            alert('Producto actualizado exitosamente');
        } else {
            await api.post('/productos', producto);
            alert('Producto creado exitosamente');
        }
        cancelarFormulario();
        cargarProductos();
    } catch (error) {
        alert('Error al guardar producto: ' + error.message);
    }
}

async function editarProducto(id) {
    const producto = productos.find(p => p.id === id);
    if (producto) {
        document.getElementById('productoId').value = producto.id;
        document.getElementById('codigoProducto').value = producto.codigo;
        document.getElementById('nombreProducto').value = producto.nombre;
        document.getElementById('descripcionProducto').value = producto.descripcion || '';
        document.getElementById('cantidadStock').value = producto.stock;
        document.getElementById('precioUnitario').value = producto.costoImportacionOrigen;
        document.getElementById('categoriaProducto').value = producto.categoriaProducto;
        document.getElementById('monedaOrigen').value = producto.monedaOrigen;
        document.getElementById('proveedor').value = producto.proveedor || '';

        document.getElementById('formularioProducto').style.display = 'block';
        document.getElementById('tituloFormulario').textContent = 'Editar Producto';
    }
}

async function eliminarProducto(id) {
    if (!confirm('¿Está seguro de eliminar este producto?')) return;

    try {
        await api.delete(`/productos/${id}`);
        alert('Producto eliminado exitosamente');
        cargarProductos();
    } catch (error) {
        alert('Error al eliminar producto: ' + error.message);
    }
}

document.addEventListener('DOMContentLoaded', cargarProductos);
