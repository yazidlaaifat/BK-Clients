package org.app.demokeyclock.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientDTO {
    private String cin;
    private String nom;
    private String prenom;
    private String telephone;
    private String adresse;
}