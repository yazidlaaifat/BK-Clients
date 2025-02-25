package org.app.demokeyclock.services;

import org.app.demokeyclock.entities.Client;
import org.app.demokeyclock.repositories.ClientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.batch.item.validator.ValidationException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class ClientValidatorTest {

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private ClientValidator clientValidator;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testValidate_withEmptyCin_shouldThrowException() {
        Client client = new Client();
        client.setCin("");
        client.setTelephone("0612345678");

        assertThatThrownBy(() -> clientValidator.validate(client))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("CIN est vide ou nul");
    }

    @Test
    public void testValidate_withInvalidCinFormat_shouldThrowException() {
        Client client = new Client();
        client.setCin("123456789"); // Mauvais format
        client.setTelephone("0612345678");

        assertThatThrownBy(() -> clientValidator.validate(client))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Format de CIN incorrect");
    }

    @Test
    public void testValidate_whenCinAlreadyExists_shouldThrowException() {
        Client client = new Client();
        client.setCin("AB123456"); // Format correct
        client.setTelephone("0612345678");

        // Simuler qu'un client existe déjà avec ce CIN
        when(clientRepository.existsById(anyString())).thenReturn(true);

        assertThatThrownBy(() -> clientValidator.validate(client))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("CIN déjà présent en base de données");
    }

    @Test
    public void testValidate_withInvalidPhoneNumber_shouldThrowException() {
        Client client = new Client();
        client.setCin("AB123456");
        client.setTelephone("0512345678"); // Mauvais préfixe

        // Simuler qu'aucun client n'existe avec ce CIN
        when(clientRepository.existsById(anyString())).thenReturn(false);

        assertThatThrownBy(() -> clientValidator.validate(client))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Format de numéro de téléphone incorrect");
    }

    @Test
    public void testValidate_withValidClient_shouldNotThrowException() throws Exception {
        Client client = new Client();
        client.setCin("AB123456");
        client.setTelephone("0612345678");

        when(clientRepository.existsById(anyString())).thenReturn(false);

        // Aucun exception n'est attendue pour un client valide
        clientValidator.validate(client);
    }
}
