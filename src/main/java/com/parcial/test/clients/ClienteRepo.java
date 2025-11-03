package com.parcial.test.clients;

import com.parcial.test.clients.entities.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClienteRepo extends JpaRepository<Client,Long> {
    List<Client> findByPais(String pais);
}
