package org.app.demokeyclock.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;


@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter @Builder @ToString
@Entity
public class Client {
    @Id
    private String cin;
    private String nom;
    private String prenom;
    private String telephone;
    private String adresse;
}
