package org.app.demokeyclock.services;

import org.app.demokeyclock.entities.Client;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.batch.item.validator.ValidationException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;

public class ClientProcessorTest {

    @Mock
    private ClientValidator clientValidator;

    @InjectMocks
    private ClientProcessor clientProcessor;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testProcess_whenValidationFails_shouldThrowException() throws Exception {
        Client client = new Client();
        client.setCin("INVALID");
        client.setTelephone("0612345678");

        // Simuler que la validation échoue
        doThrow(new ValidationException("Format de CIN incorrect"))
                .when(clientValidator).validate(client);

        assertThatThrownBy(() -> clientProcessor.process(client))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Format de CIN incorrect");
    }

    @Test
    public void testProcess_whenClientValid_shouldReturnClient() throws Exception {
        Client client = new Client();
        client.setCin("AB123456");
        client.setTelephone("0612345678");

        // Aucune exception ne sera levée par la validation
        Client processedClient = clientProcessor.process(client);

        // On s'attend à recevoir le même client
        assert(processedClient.equals(client));
    }
}
