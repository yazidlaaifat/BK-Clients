package org.app.demokeyclock.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.app.demokeyclock.entities.Client;
import org.app.demokeyclock.repositories.ClientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.validator.ValidationException;
import org.springframework.batch.item.validator.Validator;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@RequiredArgsConstructor
@Component
@Slf4j
public class ClientValidator implements Validator<Client> {


    private final ClientRepository clientRepository;

    @Override
    public void validate(Client client) throws ValidationException {
        String cin = client.getCin();
        String phoneNumber = client.getTelephone();

        log.info("Validation du client : CIN={}, Téléphone={}", cin, phoneNumber);

        // Validation du CIN
        if (!StringUtils.hasText(cin)) {
            log.warn("CIN est vide ou nul");
            throw new ValidationException("CIN est vide ou nul");
        }
        if (!cin.matches("^[A-Za-z]{2}\\d{6,7}$")) {
            log.warn("Format de CIN incorrect : {}", cin);
            throw new ValidationException("Format de CIN incorrect");
        }
        if (clientRepository.existsById(cin)) {
            log.warn("CIN déjà présent en base de données : {}", cin);
            throw new ValidationException("CIN déjà présent en base de données");
        }

        // Validation du numéro de téléphone
        if (!StringUtils.hasText(phoneNumber)) {
            log.warn("Numéro de téléphone est vide ou nul");
            throw new ValidationException("Numéro de téléphone est vide ou nul");
        }

        if (!phoneNumber.matches("^(06|07)\\d{8}$") && !phoneNumber.matches("^((\\+212)(6|7)\\d{8})$")) {
            log.warn("Format de numéro de téléphone incorrect : {}", phoneNumber);
            throw new ValidationException("Format de numéro de téléphone incorrect");
        }

        log.info("Validation réussie pour le client : CIN={}", cin);
    }
}
