package org.app.demokeyclock.services;
import org.app.demokeyclock.entities.Client;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ClientProcessor implements ItemProcessor<Client, Client> {

    @Autowired
    private CinValidator cinValidator;

    @Override
    public Client process(Client client) throws Exception {
        // Valider le CIN
        cinValidator.validate(client);

        // Autres traitements si nécessaire

        return client;
    }
}
