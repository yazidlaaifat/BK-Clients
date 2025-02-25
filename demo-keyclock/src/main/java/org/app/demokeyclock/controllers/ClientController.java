package org.app.demokeyclock.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.app.demokeyclock.dto.ClientDTO;
import org.app.demokeyclock.services.ClientService;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin("*")
@RequiredArgsConstructor
@Slf4j
public class ClientController {


    // SERVICES
    private final ClientService clientService;
    private final JobLauncher jobLauncher;
    private final Job importClientJob;

    @PostMapping("/run")
    public String runBatchJob() {
        try {
            jobLauncher.run(importClientJob, new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters());
            return "Job started successfully!";
        } catch (Exception e) {
            return "Failed to start job: " + e.getMessage();
        }
    }

    // ENDPOINT1 : Retrieve a list of all clients
    @GetMapping("/clients")
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<ClientDTO> clientList() {
        log.info("Fetching all clients");
        return clientService.getAllClients();
    }

    // ENDPOINT2: Retrieve a client by their CIN
    @GetMapping("/clients/{cin}")
    @PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
    public ResponseEntity<ClientDTO> clientByCin(@PathVariable String cin) {
        log.info("Fetching client with CIN: {}", cin);
        return clientService.getClientByCin(cin)
                .map(ResponseEntity::ok)
                .orElseGet(() -> {
                    log.warn("Client with CIN {} not found", cin);
                    return ResponseEntity.notFound().build();
                });
    }

    // ENDPOINT3: Add a new client
    @PostMapping("/clients")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ClientDTO> addClient(@RequestBody ClientDTO clientDTO) {
        try {
            log.info("Adding new client: {}", clientDTO);
            return ResponseEntity.ok(clientService.addClient(clientDTO));
        } catch (RuntimeException e) {
            log.error("Error adding client: {}", e.getMessage());
            return ResponseEntity.badRequest().body(null);
        }
    }

    // ENDPOINT4: Update an existing client based on their CIN
    @PutMapping("/clients/{cin}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ClientDTO> updateClient(@PathVariable String cin, @RequestBody ClientDTO clientDTO) {
        log.info("Updating client with CIN: {}", cin);
        return clientService.updateClient(cin, clientDTO)
                .map(ResponseEntity::ok)
                .orElseGet(() -> {
                    log.warn("Client with CIN {} not found for update", cin);
                    return ResponseEntity.notFound().build();
                });
    }

    // ENDPOINT5: Delete a client based on their CIN
    @DeleteMapping("/clients/{cin}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> deleteClient(@PathVariable String cin) {
        log.info("Deleting client with CIN: {}", cin);
        return clientService.deleteClient(cin)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    // ENDPOINT6: Filter clients based on CIN
    @GetMapping("/clients/filter")
    @PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
    public List<ClientDTO> filterClients(@RequestParam(required = false) String cin) {
        log.info("Filtering clients with CIN: {}", cin);
        return clientService.filterClients(cin);
    }
}
