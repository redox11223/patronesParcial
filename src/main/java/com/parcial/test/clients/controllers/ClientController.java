package com.parcial.test.clients.controllers;

import com.parcial.test.clients.entities.Client;
import com.parcial.test.clients.services.ClientServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/clientes")
@RequiredArgsConstructor
public class ClientController {

    private final ClientServiceImpl clientService;

    @GetMapping
    public ResponseEntity<List<Client>> getAllClients() {
        List<Client> clients = clientService.getAll();
        return ResponseEntity.ok(clients);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Client> getClient(@PathVariable Long id) {
        Client client = clientService.getById(id);
        return ResponseEntity.ok(client);
    }

    @GetMapping("/pais/{pais}")
    public ResponseEntity<List<Client>> getClientsByPais(@PathVariable String pais) {
        List<Client> clients = clientService.getByPais(pais);
        return ResponseEntity.ok(clients);
    }

    @PostMapping
    public ResponseEntity<Client> saveClient(@RequestBody Client client) {
        Client newClient = clientService.save(client);
        return ResponseEntity.status(HttpStatus.CREATED).body(newClient);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Client> updateClient(@PathVariable Long id, @RequestBody Client client) {
        // Asegurar que el ID del cliente corresponde al de la URL
        client.setId(id);
        Client updatedClient = clientService.save(client);
        return ResponseEntity.ok(updatedClient);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable Long id) {
        clientService.delete(id);
        return ResponseEntity.noContent().build();
    }
}


