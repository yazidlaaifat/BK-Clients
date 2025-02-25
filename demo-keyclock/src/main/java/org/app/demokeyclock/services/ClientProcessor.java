package org.app.demokeyclock.services;
import lombok.RequiredArgsConstructor;
import org.app.demokeyclock.entities.Client;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ClientProcessor implements ItemProcessor<Client, Client> {


    private final ClientValidator clientValidator;

    @Override
    public Client process(Client client) throws Exception {
        // Valider le CIN
        clientValidator.validate(client);

        return client;
    }
}
