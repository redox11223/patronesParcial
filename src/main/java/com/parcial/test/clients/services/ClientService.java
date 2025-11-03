package com.parcial.test.clients.services;

import com.parcial.test.clients.entities.Client;

import java.util.List;

public interface ClientService {
    Client save(Client client);
    List<Client> getAll();
    Client getById(Long id);
    List<Client> getByPais(String pais);
}