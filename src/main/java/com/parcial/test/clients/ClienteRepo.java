package com.parcial.test.clients;

import com.parcial.test.clients.entities.Client;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepo extends JpaRepository<Client,Long> {
}
