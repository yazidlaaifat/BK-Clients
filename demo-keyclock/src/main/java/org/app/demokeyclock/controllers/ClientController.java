package org.app.demokeyclock.controllers;

import org.app.demokeyclock.entities.Client;
import org.app.demokeyclock.repositories.ClientRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@CrossOrigin("*")
public class ClientController {
    private final ClientRepository clientRepository;

    public ClientController(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @GetMapping("/clients")
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<Client> clientList() {
        return clientRepository.findAll();
    }

    @GetMapping("/clients/{cin}")
    @PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
    public ResponseEntity<Client> clientByCin(@PathVariable String cin) {
        Optional<Client> client = Optional.ofNullable(clientRepository.findByCin(cin));
        return client.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/clients")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Client> addClient(@RequestBody Client client) {
        if (clientRepository.existsById(client.getCin())) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(clientRepository.save(client));
    }

    @PutMapping("/clients/{cin}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Client> updateClient(@PathVariable String cin, @RequestBody Client clientDetails) {
        return clientRepository.findById(cin).map(client -> {
            client.setNom(clientDetails.getNom());
            client.setPrenom(clientDetails.getPrenom());
            client.setTelephone(clientDetails.getTelephone());
            client.setAdresse(clientDetails.getAdresse());
            return ResponseEntity.ok(clientRepository.save(client));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/clients/{cin}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> deleteClient(@PathVariable String cin) {
        if (!clientRepository.existsById(cin)) {
            return ResponseEntity.notFound().build();
        }
        clientRepository.deleteById(cin);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/clients/filter")
    @PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
    public List<Client> filterClients(@RequestParam(required = false) String cin) {
        if (cin != null) {
            // Filtrage dynamique : Si 'cin' est fourni, on retourne le client correspondant
            Client client = clientRepository.findByCin(cin);
            return client != null ? List.of(client) : List.of();  // Retourne une liste avec le client ou vide si non trouvé
        } else {
            // Si aucun 'cin' n'est fourni, retourne tous les clients
            return clientRepository.findAll();
        }
    }

}
