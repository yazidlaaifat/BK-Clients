package org.app.demokeyclock.services;

import org.app.demokeyclock.dto.ClientDTO;
import org.app.demokeyclock.entities.Client;
import org.app.demokeyclock.mapper.ClientMapper;
import org.app.demokeyclock.repositories.ClientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Active Mockito pour les tests
class ClientServiceTest {

    @Mock // Mock du repository
    private ClientRepository clientRepository;

    @Mock // Mock du mapper
    private ClientMapper clientMapper;

    @InjectMocks // Injecte les mocks dans le service
    private ClientService clientService;

    private Client client;
    private ClientDTO clientDTO;

    @BeforeEach
    void setUp() {
        client = new Client("12345", "John", "Doe", "0600000000", "Adresse 1");
        clientDTO = new ClientDTO("12345", "John", "Doe", "0600000000", "Adresse 1");
    }

    @Test
    void testGetAllClients() {
        when(clientRepository.findAll()).thenReturn(List.of(client));
        when(clientMapper.toDTO(client)).thenReturn(clientDTO);

        List<ClientDTO> result = clientService.getAllClients();

        assertEquals(1, result.size());
        assertEquals("John", result.get(0).getNom());
    }

    @Test
    void testGetClientByCin_Found() {
        when(clientRepository.findByCin("12345")).thenReturn(client);
        when(clientMapper.toDTO(client)).thenReturn(clientDTO);

        Optional<ClientDTO> result = clientService.getClientByCin("12345");

        assertTrue(result.isPresent());
        assertEquals("John", result.get().getNom());
    }

    @Test
    void testGetClientByCin_NotFound() {
        when(clientRepository.findByCin("12345")).thenReturn(null);

        Optional<ClientDTO> result = clientService.getClientByCin("12345");

        assertFalse(result.isPresent());
    }

    @Test
    void testAddClient_Success() {
        when(clientRepository.existsById("12345")).thenReturn(false);
        when(clientMapper.toEntity(clientDTO)).thenReturn(client);
        when(clientRepository.save(client)).thenReturn(client);
        when(clientMapper.toDTO(client)).thenReturn(clientDTO);

        ClientDTO result = clientService.addClient(clientDTO);

        assertNotNull(result);
        assertEquals("12345", result.getCin());
    }

    @Test
    void testAddClient_AlreadyExists() {
        when(clientRepository.existsById("12345")).thenReturn(true);

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            clientService.addClient(clientDTO);
        });

        assertEquals("Client avec CIN 12345 existe déjà.", thrown.getMessage());
    }

    @Test
    void testUpdateClient_Success() {
        when(clientRepository.findById("12345")).thenReturn(Optional.of(client));
        when(clientRepository.save(any(Client.class))).thenReturn(client);
        when(clientMapper.toDTO(client)).thenReturn(clientDTO);

        Optional<ClientDTO> result = clientService.updateClient("12345", clientDTO);

        assertTrue(result.isPresent());
        assertEquals("John", result.get().getNom());
    }

    @Test
    void testUpdateClient_NotFound() {
        when(clientRepository.findById("12345")).thenReturn(Optional.empty());

        Optional<ClientDTO> result = clientService.updateClient("12345", clientDTO);

        assertFalse(result.isPresent());
    }

    @Test
    void testDeleteClient_Success() {
        when(clientRepository.existsById("12345")).thenReturn(true);
        doNothing().when(clientRepository).deleteById("12345");

        boolean result = clientService.deleteClient("12345");

        assertTrue(result);
        verify(clientRepository, times(1)).deleteById("12345");
    }

    @Test
    void testDeleteClient_NotFound() {
        when(clientRepository.existsById("12345")).thenReturn(false);

        boolean result = clientService.deleteClient("12345");

        assertFalse(result);
    }
}
