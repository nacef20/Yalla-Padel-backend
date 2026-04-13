package tn.esprit.tournoi.dto;

import lombok.Builder;
import lombok.Data;
import tn.esprit.tournoi.entity.StatutTournoi;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

// ──────────────────────────────────────────────────────────
// DTO renvoyé dans les réponses HTTP
// ──────────────────────────────────────────────────────────
@Data
@Builder
public class TournoiResponse {

    private Long id;
    private String nom;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private String lieu;
    private int capaciteMax;
    private int nbParticipants;
    private boolean complet;
    private String description;
    private StatutTournoi statut;
    private Double prixInscription;
    private Long terrainId;
    private List<Long> participantIds;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Informations du terrain enrichies via OpenFeign
    private String terrainNom;
    private String terrainAdresse;
}
