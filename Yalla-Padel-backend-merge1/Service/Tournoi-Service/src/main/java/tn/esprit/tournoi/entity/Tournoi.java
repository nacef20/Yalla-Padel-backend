package tn.esprit.tournoi.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tournoi")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tournoi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom du tournoi est obligatoire")
    @Size(min = 3, max = 100)
    @Column(nullable = false)
    private String nom;

    @NotNull(message = "La date de début est obligatoire")
    private LocalDate dateDebut;

    @NotNull(message = "La date de fin est obligatoire")
    private LocalDate dateFin;

    @NotBlank(message = "Le lieu est obligatoire")
    private String lieu;

    @Min(value = 2, message = "Minimum 2 participants")
    @Max(value = 128, message = "Maximum 128 participants")
    private int capaciteMax;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutTournoi statut;

    @DecimalMin(value = "0.0", inclusive = true)
    private Double prixInscription;

    /** ID du gagnant désigné à la clôture */
    private String gagnantId;

    @ElementCollection
    @CollectionTable(name = "tournoi_participants",
            joinColumns = @JoinColumn(name = "tournoi_id"))
    @Column(name = "user_id")
    @Builder.Default
    private List<String> participantIds = new ArrayList<>();

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt  = LocalDateTime.now();
        if (statut == null) statut = StatutTournoi.OUVERT;
        if (participantIds == null) participantIds = new ArrayList<>();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        if (participantIds == null) participantIds = new ArrayList<>();
    }

    @PostLoad
    protected void onLoad() {
        if (participantIds == null) participantIds = new ArrayList<>();
    }

    // ----------------------------------------------------------------
    // Méthodes métier
    // ----------------------------------------------------------------
    public boolean ajouterParticipant(String userId) {
        if (participantIds.size() >= capaciteMax) return false;
        if (participantIds.contains(userId))      return false;
        participantIds.add(userId);
        return true;
    }

    public boolean retirerParticipant(String userId) {
        return participantIds.remove(userId);
    }

    public int getNbParticipants() {
        return participantIds.size();
    }

    public boolean estComplet() {
        return participantIds.size() >= capaciteMax;
    }
}