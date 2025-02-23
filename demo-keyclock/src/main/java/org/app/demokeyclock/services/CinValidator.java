package org.app.demokeyclock.services;
import org.app.demokeyclock.entities.Client;
import org.app.demokeyclock.repositories.ClientRepository;
import org.springframework.batch.item.validator.ValidationException;
import org.springframework.batch.item.validator.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class CinValidator implements Validator<Client> {

    @Autowired
    private ClientRepository clientRepository; // Assurez-vous d'avoir un repository pour accéder à la base de données

    @Override
    public void validate(Client client) throws ValidationException {
        String cin = client.getCin();

        // Vérifier si le CIN est nul ou vide
        if (!StringUtils.hasText(cin)) {
            throw new ValidationException("CIN est vide ou nul");
        }

        // Vérifier le format du CIN (par exemple, doit être alphanumérique)
        if (!cin.matches("[A-Za-z0-9]+")) {
            throw new ValidationException("Format de CIN incorrect");
        }

        // Vérifier la taille du CIN (par exemple, doit être de 8 caractères)
        if (cin.length() != 8) {
            throw new ValidationException("Taille de CIN incorrecte");
        }

        // Vérifier les caractères non valides (par exemple, pas de caractères spéciaux)
        if (!cin.matches("[A-Za-z0-9]*")) {
            throw new ValidationException("CIN contient des caractères non valides");
        }

        // Vérifier les doublons en base de données
        if (clientRepository.existsById(cin)) {
            throw new ValidationException("CIN déjà présent en base de données");
        }
    }
}

