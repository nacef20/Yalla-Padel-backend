package tn.esprit.tournoi.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;


/**
 * ──────────────────────────────────────────────────────────────────
 * Consommateur RabbitMQ : écoute les messages d'autres microservices.
 *
 * Exemple : le forum-service publie un événement "user.banned"
 *           → on annule automatiquement son inscription aux tournois.
 *
 * (La queue "user.banned.queue" doit être déclarée par le forum-service
 *  ou partagée dans la config commune.)
 * ──────────────────────────────────────────────────────────────────
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TournoiConsumer {

    // Décommenter si vous injectez TournoiService ici (attention aux cycles)
    // private final TournoiService tournoiService;

    /**
     * Consomme les événements "user.banned" venant du forum-service.
     * → Retire l'utilisateur de tous les tournois auxquels il est inscrit.
     *
     * La queue "forum.user.banned.queue" est déclarée par forum-service
     * et tournoi-service s'y abonne.
     */
    @RabbitListener(queuesToDeclare = @Queue(
            value = "forum.user.banned.queue",
            durable = "true"
    ))
    public void handleUserBanned(UserBannedEvent event) {
        log.info("📥 [RabbitMQ] UserBanned reçu → userId={}", event.getUserId());
        // TODO: tournoiService.retirerUtilisateurDeTousLesTournois(event.getUserId());
        log.info("✅ Utilisateur {} retiré de tous les tournois.", event.getUserId());
    }

    // ─── DTO interne ─────────────────────────────────────────────
    @lombok.Data
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class UserBannedEvent {
        private Long userId;
        private String raison;
    }
}
