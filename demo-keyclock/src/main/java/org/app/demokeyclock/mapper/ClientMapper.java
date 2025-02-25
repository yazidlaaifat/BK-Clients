package org.app.demokeyclock.mapper;

import lombok.extern.slf4j.Slf4j;
import org.app.demokeyclock.dto.ClientDTO;
import org.app.demokeyclock.entities.Client;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ClientMapper {

    // CONVERTS : ENTITY TO DTO
    public ClientDTO toDTO(Client client) {
        if (client == null) {
            log.warn("Tentative de conversion d'un client null en DTO.");
            return null;
        }
        ClientDTO dto = new ClientDTO(
                client.getCin(),
                client.getNom(),
                client.getPrenom(),
                client.getTelephone(),
                client.getAdresse()
        );
        log.debug("Conversion d'Entity to DTO effectuée pour le client avec CIN : {}", client.getCin());
        return dto;
    }

    // CONVERTS: DTO TO ENTITY
    public Client toEntity(ClientDTO clientDTO) {
        if (clientDTO == null) {
            log.warn("Tentative de conversion d'un DTO null en entity.");
            return null;
        }
        Client client = new Client(
                clientDTO.getCin(),
                clientDTO.getNom(),
                clientDTO.getPrenom(),
                clientDTO.getTelephone(),
                clientDTO.getAdresse()
        );
        log.debug("Conversion de DTO to Entity effectuée pour le client avec CIN : {}", clientDTO.getCin());
        return client;
    }
}
