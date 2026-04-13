package tn.esprit.tournoi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import tn.esprit.tournoi.entity.StatutTournoi;
import tn.esprit.tournoi.entity.Tournoi;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TournoiRepository extends JpaRepository<Tournoi, Long> {

    List<Tournoi> findByStatut(StatutTournoi statut);

    List<Tournoi> findByLieuContainingIgnoreCase(String lieu);

    List<Tournoi> findByDateDebutBetween(LocalDate debut, LocalDate fin);

    @Query("SELECT t FROM Tournoi t WHERE :userId MEMBER OF t.participantIds")
    List<Tournoi> findByParticipantId(Long userId);

    @Query("SELECT t FROM Tournoi t WHERE SIZE(t.participantIds) < t.capaciteMax AND t.statut = 'OUVERT'")
    List<Tournoi> findTournoisDisponibles();

    boolean existsByNomAndDateDebut(String nom, LocalDate dateDebut);
}
