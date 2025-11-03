package com.parcial.test.clients.services;

import com.parcial.test.clients.entities.Client;
import com.parcial.test.clients.ClienteRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final ClienteRepo clienteRepo;

    @Override
    public Client save(Client client) {
        return clienteRepo.save(client);
    }

    @Override
    public List<Client> getAll() {
        return clienteRepo.findAll();
    }

    @Override
    public Client getById(Long id) {
        return clienteRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado: " + id));
    }

    @Override
    public List<Client> getByPais(String pais) {
        return clienteRepo.findByPais(pais);
    }
}

