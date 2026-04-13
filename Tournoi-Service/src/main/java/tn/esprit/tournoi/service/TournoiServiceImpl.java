package tn.esprit.tournoi.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tn.esprit.tournoi.dto.TerrainResponse;
import tn.esprit.tournoi.entity.StatutTournoi;
import tn.esprit.tournoi.entity.Tournoi;
import tn.esprit.tournoi.feign.ReservationFeignClient;
import tn.esprit.tournoi.messaging.TournoiEvents;
import tn.esprit.tournoi.messaging.TournoiProducer;
import tn.esprit.tournoi.repository.TournoiRepository;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class TournoiServiceImpl implements ITournoiService {

    private final TournoiRepository      tournoiRepository;
    private final ReservationFeignClient reservationClient;  // OpenFeign → reservation-service
    private final TournoiProducer        tournoiProducer;    // RabbitMQ

    // ================================================================
    //  CRUD
    // ================================================================

    @Override
    public Tournoi addTournoi(Tournoi tournoi) {
        if (tournoi.getTerrainId() != null) {
            log.info("[Feign DEBUG] Appel terrain ID={}", tournoi.getTerrainId());
            try {
                TerrainResponse terrain = reservationClient.getTerrainById(tournoi.getTerrainId());
                log.info("[Feign DEBUG] Terrain reçu: id={}, nom='{}', disponible={}",
                        terrain.getId(), terrain.getNom(), terrain.isDisponible());
                if (!terrain.isDisponible()) {
                    throw new RuntimeException(
                            "Le terrain '" + terrain.getNom() + "' n'est pas disponible."
                    );
                }
                log.info("[Feign] Terrain '{}' confirmé disponible.", terrain.getNom());
            } catch (RuntimeException e) {
                log.error("[Feign DEBUG] EXCEPTION: type={}, message={}",
                        e.getClass().getName(), e.getMessage(), e);
                throw e;
            }
        }

        Tournoi saved = tournoiRepository.save(tournoi);

        try {
            tournoiProducer.publishTournoiCreated(
                    new TournoiEvents.TournoiCreatedEvent(
                            saved.getId(), saved.getNom(), saved.getDateDebut(),
                            saved.getLieu(), saved.getCapaciteMax(), saved.getPrixInscription()
                    )
            );
            log.info("[RabbitMQ] Événement création publié pour '{}'", saved.getNom());
        } catch (Exception e) {
            log.warn("[RabbitMQ] Publication création échouée (non bloquant) : {}", e.getMessage());
        }

        return saved;
    }

    @Override
    public Tournoi updateTournoi(Tournoi tournoi) {
        return tournoiRepository.save(tournoi);
    }

    @Override
    public List<Tournoi> getAllTournois() {
        return tournoiRepository.findAll();
    }

    @Override
    public Tournoi getTournoiById(Long id) {
        return tournoiRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tournoi introuvable — id=" + id));
    }

    @Override
    public void deleteTournoi(Long id) {
        tournoiRepository.deleteById(id);
    }

    // ================================================================
    //  MÉTIER
    // ================================================================

    @Override
    public Tournoi inscrireJoueur(Long tournoiId, Long userId) {
        Tournoi tournoi = getTournoiById(tournoiId);

        if (tournoi.getStatut() != StatutTournoi.OUVERT) {
            throw new RuntimeException(
                    "Le tournoi '" + tournoi.getNom() + "' n'accepte plus d'inscriptions (statut: "
                            + tournoi.getStatut() + ")."
            );
        }

        boolean ajout = tournoi.ajouterParticipant(userId);
        if (!ajout) {
            throw new RuntimeException(
                    "Impossible d'inscrire le joueur " + userId +
                            " : tournoi complet ou joueur déjà inscrit."
            );
        }

        if (tournoi.estComplet()) {
            tournoi.setStatut(StatutTournoi.COMPLET);
            log.info("[Métier] Tournoi '{}' marqué COMPLET ({}/{}).",
                    tournoi.getNom(), tournoi.getNbParticipants(), tournoi.getCapaciteMax());
        }

        Tournoi saved = tournoiRepository.save(tournoi);

        // --- COMMUNICATION 2 : RabbitMQ → Inscription ---
        try {
            tournoiProducer.publishInscription(
                    new TournoiEvents.InscriptionEvent(
                            saved.getId(), saved.getNom(), userId,
                            saved.getNbParticipants(), saved.getCapaciteMax()
                    )
            );
            log.info("[RabbitMQ] Inscription publiée — userId={} tournoi='{}'",
                    userId, saved.getNom());
        } catch (Exception e) {
            log.warn("[RabbitMQ] Publication inscription échouée (non bloquant) : {}", e.getMessage());
        }

        return saved;
    }

    @Override
    public Tournoi desinscrireJoueur(Long tournoiId, Long userId) {
        Tournoi tournoi = getTournoiById(tournoiId);

        if (tournoi.getStatut() == StatutTournoi.EN_COURS
                || tournoi.getStatut() == StatutTournoi.TERMINE) {
            throw new RuntimeException(
                    "Désinscription impossible : tournoi " + tournoi.getStatut() + "."
            );
        }

        boolean retrait = tournoi.retirerParticipant(userId);
        if (!retrait) {
            throw new RuntimeException(
                    "Le joueur " + userId + " n'est pas inscrit à ce tournoi."
            );
        }

        if (tournoi.getStatut() == StatutTournoi.COMPLET) {
            tournoi.setStatut(StatutTournoi.OUVERT);
            log.info("[Métier] Tournoi '{}' repassé à OUVERT.", tournoi.getNom());
        }

        return tournoiRepository.save(tournoi);
    }

    @Override
    public Tournoi designerGagnant(Long tournoiId, Long userId) {
        Tournoi tournoi = getTournoiById(tournoiId);

        if (!tournoi.getParticipantIds().contains(userId)) {
            throw new RuntimeException(
                    "Le joueur " + userId +
                            " n'est pas participant au tournoi '" + tournoi.getNom() + "'."
            );
        }

        tournoi.setGagnantId(userId);
        tournoi.setStatut(StatutTournoi.TERMINE);
        log.info("[Métier] Tournoi '{}' clôturé. Gagnant userId={}.", tournoi.getNom(), userId);

        Tournoi saved = tournoiRepository.save(tournoi);

        // --- COMMUNICATION 2 : RabbitMQ → Changement statut + libération terrain ---
        try {
            // Publie le changement de statut
            tournoiProducer.publishStatutChanged(
                    new TournoiEvents.StatutChangedEvent(
                            saved.getId(), saved.getNom(), saved.getTerrainId(), "EN_COURS", "TERMINE"
                    )
            );
            log.info("[RabbitMQ] Statut TERMINE publié pour '{}'", saved.getNom());

            // Libère le terrain via Feign maintenant que le tournoi est terminé
            if (saved.getTerrainId() != null) {
                reservationClient.updateDisponibilite(saved.getTerrainId(), true);
                log.info("[Feign] Terrain {} libéré après clôture du tournoi.", saved.getTerrainId());
            }
        } catch (Exception e) {
            log.warn("[RabbitMQ/Feign] Opération post-clôture échouée (non bloquant) : {}", e.getMessage());
        }

        return saved;
    }

    @Override
    public List<Tournoi> getTournoisByStatut(String statut) {
        StatutTournoi s = StatutTournoi.valueOf(statut.toUpperCase());
        return tournoiRepository.findByStatut(s);
    }
}