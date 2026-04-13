package tn.esprit.tournoi.messaging;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Messages publiés sur RabbitMQ — doivent être Serializable.
 */
public class TournoiEvents {

    /** Publié quand un tournoi est créé */
    @Data @NoArgsConstructor @AllArgsConstructor
    public static class TournoiCreatedEvent implements Serializable {
        private Long tournoiId;
        private String nom;
        private LocalDate dateDebut;
        private String lieu;
        private int capaciteMax;
        private Double prixInscription;
    }

    /** Publié quand un utilisateur s'inscrit à un tournoi */
    @Data @NoArgsConstructor @AllArgsConstructor
    public static class InscriptionEvent implements Serializable {
        private Long tournoiId;
        private String tournoiNom;
        private Long userId;
        private int nbParticipantsActuel;
        private int capaciteMax;
    }

    /** Publié quand le statut d'un tournoi change */
    @Data @NoArgsConstructor @AllArgsConstructor
    public static class StatutChangedEvent implements Serializable {
        private Long tournoiId;
        private String tournoiNom;
        private Long terrainId; // Ajouté pour faciliter la communication avec terrain-service
        private String ancienStatut;
        private String nouveauStatut;
    }
}
