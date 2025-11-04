package com.parcial.test.clients.services;

import com.parcial.test.clients.entities.Client;
import com.parcial.test.clients.ClienteRepo;
import com.parcial.test.exceptions.ResourceNotFoundException;
import com.parcial.test.exceptions.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final ClienteRepo clienteRepo;

    @Override
    public Client save(Client client) {
        validateClient(client);
        return clienteRepo.save(client);
    }

    @Override
    public List<Client> getAll() {
        return clienteRepo.findAll();
    }

    @Override
    public Client getById(Long id) {
        return clienteRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", String.valueOf(id)));
    }

    @Override
    public List<Client> getByPais(String pais) {
        if (pais == null || pais.trim().isEmpty()) {
            throw new ValidationException("pais", "El país no puede estar vacío");
        }
        return clienteRepo.findByPais(pais);
    }

    @Override
    public void delete(Long id) {
        if (!clienteRepo.existsById(id)) {
            throw new ResourceNotFoundException("Cliente", String.valueOf(id));
        }
        clienteRepo.deleteById(id);
    }

    private void validateClient(Client client) {
        if (client.getNombre() == null || client.getNombre().trim().isEmpty()) {
            throw new ValidationException("nombre", "El nombre es obligatorio");
        }
        if (client.getDocumento() == null || client.getDocumento().trim().isEmpty()) {
            throw new ValidationException("documento", "El documento es obligatorio");
        }
        if (client.getPais() == null || client.getPais().trim().isEmpty()) {
            throw new ValidationException("pais", "El país es obligatorio");
        }
    }
}

