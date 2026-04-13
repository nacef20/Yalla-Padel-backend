package tn.esprit.tournoi.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tn.esprit.tournoi.dto.UserResponse;
import tn.esprit.tournoi.entity.StatutTournoi;
import tn.esprit.tournoi.entity.Tournoi;
import tn.esprit.tournoi.feign.UserClient;
import tn.esprit.tournoi.messaging.TournoiEvents;
import tn.esprit.tournoi.messaging.TournoiProducer;
import tn.esprit.tournoi.repository.TournoiRepository;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class TournoiServiceImpl implements ITournoiService {

    private final TournoiRepository tournoiRepository;
    private final UserClient userClient;
    private final TournoiProducer tournoiProducer;

    @Override
    public Tournoi addTournoi(Tournoi tournoi) {
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

    @Override
    public Tournoi inscrireJoueur(Long tournoiId, String userId, String userEmail) {
        Tournoi tournoi = getTournoiById(tournoiId);

        if (tournoi.getStatut() != StatutTournoi.OUVERT) {
            throw new RuntimeException(
                    "Le tournoi '" + tournoi.getNom() + "' n'accepte plus d'inscriptions (statut: "
                            + tournoi.getStatut() + ")."
            );
        }

        if (userEmail == null || userEmail.isBlank()) {
            throw new RuntimeException("Impossible de récupérer l'email du compte Keycloak pour userId=" + userId);
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

        try {
            tournoiProducer.publishInscription(
                    new TournoiEvents.InscriptionEvent(
                            saved.getId(), saved.getNom(), userId,
                        userEmail,
                            saved.getNbParticipants(), saved.getCapaciteMax()
                    )
            );
            log.info("[RabbitMQ] Inscription publiée — userId={} email={} tournoi='{}'",
                    userId, userEmail, saved.getNom());
        } catch (Exception e) {
            log.warn("[RabbitMQ] Publication inscription échouée (non bloquant) : {}", e.getMessage());
        }

        return saved;
    }

    @Override
    public Tournoi desinscrireJoueur(Long tournoiId, String userId) {
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
    public Tournoi designerGagnant(Long tournoiId, String userId) {
        Tournoi tournoi = getTournoiById(tournoiId);

        if (!tournoi.getParticipantIds().contains(userId)) {
            throw new RuntimeException(
                    "Le joueur " + userId +
                            " n'est pas participant au tournoi '" + tournoi.getNom() + "'."
            );
        }

        UserResponse winner = getUserDetails(userId);

        tournoi.setGagnantId(userId);
        tournoi.setStatut(StatutTournoi.TERMINE);
        log.info("[Métier] Tournoi '{}' clôturé. Gagnant userId={}.", tournoi.getNom(), userId);

        Tournoi saved = tournoiRepository.save(tournoi);

        try {
            tournoiProducer.publishStatutChanged(
                    new TournoiEvents.StatutChangedEvent(
                            saved.getId(), saved.getNom(), "EN_COURS", "TERMINE"
                    )
            );
            tournoiProducer.publishGagnantDesigne(
                    new TournoiEvents.GagnantDesigneEvent(
                            saved.getId(),
                            saved.getNom(),
                            userId,
                            winner.getUsername(),
                            winner.getFirstName(),
                            winner.getLastName(),
                            winner.getEmail()
                    )
            );
            log.info("[RabbitMQ] Statut TERMINE publié pour '{}' (gagnantId={})", saved.getNom(), userId);
        } catch (Exception e) {
            log.warn("[RabbitMQ/Feign] Opération post-clôture échouée (non bloquant) : {}", e.getMessage());
        }

        return saved;
    }

    private UserResponse getUserDetails(String userId) {
        try {
            UserResponse user = userClient.getUserById(userId);
            if (user == null || user.getId() == null) {
                throw new RuntimeException("Utilisateur introuvable pour userId=" + userId);
            }
            return user;
        } catch (Exception e) {
            throw new RuntimeException("Impossible de charger l'utilisateur depuis userservice pour userId=" + userId, e);
        }
    }

    @Override
    public List<Tournoi> getTournoisByStatut(String statut) {
        StatutTournoi s = StatutTournoi.valueOf(statut.toUpperCase());
        return tournoiRepository.findByStatut(s);
    }
}