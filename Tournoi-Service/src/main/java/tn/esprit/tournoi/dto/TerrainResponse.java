package tn.esprit.tournoi.dto;

import lombok.Data;

/**
 * DTO reçu depuis le microservice "reservation-service" via OpenFeign.
 * Représente les infos d'un terrain.
 */
@Data
public class TerrainResponse {
    private Long id;
    private String nom;
    private String adresse;
    private String type;       // ex: INDOOR / OUTDOOR
    private boolean disponible;
    private Double prixHeure;
}
