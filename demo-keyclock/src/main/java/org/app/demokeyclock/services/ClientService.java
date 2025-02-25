package org.app.demokeyclock.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.app.demokeyclock.dto.ClientDTO;
import org.app.demokeyclock.entities.Client;
import org.app.demokeyclock.mapper.ClientMapper;
import org.app.demokeyclock.repositories.ClientRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;

    // Retrieves a list of all clients in DTO format
    public List<ClientDTO> getAllClients() {
        log.info("Fetching all clients");
        List<ClientDTO> clients = clientRepository.findAll().stream()
                .map(clientMapper::toDTO)
                .collect(Collectors.toList());
        log.debug("Found {} clients", clients.size());
        return clients;
    }

    // Retrieves a client by their CIN
    public Optional<ClientDTO> getClientByCin(String cin) {
        log.info("Fetching client with CIN: {}", cin);
        Optional<ClientDTO> clientDTO = Optional.ofNullable(clientRepository.findByCin(cin))
                .map(clientMapper::toDTO);
        if (clientDTO.isPresent()) {
            log.debug("Client found: {}", clientDTO.get());
        } else {
            log.warn("No client found with CIN: {}", cin);
        }
        return clientDTO;
    }

    // Adds a new client to the database
    public ClientDTO addClient(ClientDTO clientDTO) {
        log.info("Adding new client with CIN: {}", clientDTO.getCin());
        if (clientRepository.existsById(clientDTO.getCin())) {
            log.error("Client with CIN {} already exists", clientDTO.getCin());
            throw new RuntimeException("Client avec CIN " + clientDTO.getCin() + " existe déjà.");
        }
        Client client = clientMapper.toEntity(clientDTO);
        ClientDTO savedClient = clientMapper.toDTO(clientRepository.save(client));
        log.debug("Client added successfully: {}", savedClient);
        return savedClient;
    }

    // Updates an existing client based on CIN.
    public Optional<ClientDTO> updateClient(String cin, ClientDTO clientDTO) {
        log.info("Updating client with CIN: {}", cin);
        Optional<ClientDTO> updatedClient = clientRepository.findById(cin).map(existingClient -> {
            existingClient.setNom(clientDTO.getNom());
            existingClient.setPrenom(clientDTO.getPrenom());
            existingClient.setTelephone(clientDTO.getTelephone());
            existingClient.setAdresse(clientDTO.getAdresse());
            Client updated = clientRepository.save(existingClient);
            return clientMapper.toDTO(updated);
        });
        if (updatedClient.isPresent()) {
            log.debug("Client updated: {}", updatedClient.get());
        } else {
            log.warn("Client with CIN {} not found for update", cin);
        }
        return updatedClient;
    }

    // Deletes a client based on their CIN.
    public boolean deleteClient(String cin) {
        log.info("Deleting client with CIN: {}", cin);
        if (!clientRepository.existsById(cin)) {
            log.warn("Cannot delete client. No client found with CIN: {}", cin);
            return false;
        }
        clientRepository.deleteById(cin);
        log.debug("Client with CIN {} deleted", cin);
        return true;
    }

    // Filters clients based on CIN
    public List<ClientDTO> filterClients(String cin) {
        log.info("Filtering clients with CIN: {}", cin);
        if (cin != null) {
            Client client = clientRepository.findByCin(cin);
            if (client != null) {
                log.debug("Client found for filter: {}", client);
                return List.of(clientMapper.toDTO(client));
            } else {
                log.warn("No client found for filter with CIN: {}", cin);
                return List.of();
            }
        } else {
            log.debug("No CIN provided, returning all clients");
            return getAllClients();
        }
    }
}
