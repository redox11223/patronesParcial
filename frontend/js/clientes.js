let clientes = [];

async function cargarClientes() {
    try {
        clientes = await api.get('/clientes');
        mostrarClientes();
    } catch (error) {
        alert('Error al cargar clientes: ' + error.message);
    }
}

function mostrarClientes() {
    const container = document.getElementById('clientesTable');

    if (clientes.length === 0) {
        container.innerHTML = '<p class="loading">No hay clientes registrados</p>';
        return;
    }

    let html = `
        <table>
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Nombre</th>
                    <th>Documento</th>
                    <th>Teléfono</th>
                    <th>País</th>
                    <th>Acciones</th>
                </tr>
            </thead>
            <tbody>
    `;

    clientes.forEach(cliente => {
        html += `
            <tr>
                <td>${cliente.id}</td>
                <td>${cliente.nombre}</td>
                <td>${cliente.documento}</td>
                <td>${cliente.telefono || 'N/A'}</td>
                <td>${cliente.pais}</td>
                <td>
                    <button onclick="editarCliente(${cliente.id})" style="background: #ffc107; color: white;">✏️ Editar</button>
                    <button onclick="eliminarCliente(${cliente.id})" style="background: #dc3545; color: white;">🗑️ Eliminar</button>
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
    document.getElementById('formularioCliente').style.display = 'block';
    document.getElementById('tituloFormulario').textContent = 'Nuevo Cliente';
    document.getElementById('clienteForm').reset();
    document.getElementById('clienteId').value = '';
}

function cancelarFormulario() {
    document.getElementById('formularioCliente').style.display = 'none';
    document.getElementById('clienteForm').reset();
}

async function guardarCliente(event) {
    event.preventDefault();

    const cliente = {
        nombre: document.getElementById('nombre').value,
        documento: document.getElementById('documento').value,
        telefono: document.getElementById('telefono').value,
        pais: document.getElementById('pais').value
    };

    try {
        const id = document.getElementById('clienteId').value;
        if (id) {
            await api.put(`/clientes/${id}`, cliente);
            alert('Cliente actualizado exitosamente');
        } else {
            await api.post('/clientes', cliente);
            alert('Cliente creado exitosamente');
        }
        cancelarFormulario();
        cargarClientes();
    } catch (error) {
        alert('Error al guardar cliente: ' + error.message);
    }
}

async function editarCliente(id) {
    const cliente = clientes.find(c => c.id === id);
    if (cliente) {
        document.getElementById('clienteId').value = cliente.id;
        document.getElementById('nombre').value = cliente.nombre;
        document.getElementById('documento').value = cliente.documento;
        document.getElementById('telefono').value = cliente.telefono || '';
        document.getElementById('pais').value = cliente.pais;

        document.getElementById('formularioCliente').style.display = 'block';
        document.getElementById('tituloFormulario').textContent = 'Editar Cliente';
    }
}

async function eliminarCliente(id) {
    if (!confirm('¿Está seguro de eliminar este cliente?')) return;

    try {
        await api.delete(`/clientes/${id}`);
        alert('Cliente eliminado exitosamente');
        cargarClientes();
    } catch (error) {
        alert('Error al eliminar cliente: ' + error.message);
    }
}

document.addEventListener('DOMContentLoaded', cargarClientes);
