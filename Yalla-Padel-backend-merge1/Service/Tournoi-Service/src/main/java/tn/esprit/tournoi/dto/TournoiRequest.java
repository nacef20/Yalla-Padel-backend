package tn.esprit.tournoi.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import tn.esprit.tournoi.entity.StatutTournoi;

import java.time.LocalDate;

// ──────────────────────────────────────────────────────────
// DTO reçu lors de la création / mise à jour d'un tournoi
// ──────────────────────────────────────────────────────────
@Data
public class TournoiRequest {

    @NotBlank(message = "Le nom est obligatoire")
    private String nom;

    @NotNull
    private LocalDate dateDebut;

    @NotNull
    private LocalDate dateFin;

    @NotBlank
    private String lieu;

    @Min(2) @Max(128)
    private int capaciteMax;

    private String description;

    @DecimalMin("0.0")
    private Double prixInscription;

    private StatutTournoi statut;
}
