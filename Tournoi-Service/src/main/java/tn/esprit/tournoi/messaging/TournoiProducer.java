package tn.esprit.tournoi.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

/**
 * Producteur RabbitMQ : publie des événements vers l'exchange "tournoi.exchange".
 * Les autres microservices (forum, notification, recrutement…) consomment ces events.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TournoiProducer {

    private final RabbitTemplate rabbitTemplate;

    /**
     * Publie un event "tournoi créé" → forum-service peut créer un post automatique.
     */
    public void publishTournoiCreated(TournoiEvents.TournoiCreatedEvent event) {
        log.info("📤 [RabbitMQ] Publishing TournoiCreated → {}", event.getNom());
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.EXCHANGE,
            RabbitMQConfig.RK_CREATED,
            event
        );
    }

    /**
     * Publie un event "inscription" → notification-service envoie un email de confirmation.
     */
    public void publishInscription(TournoiEvents.InscriptionEvent event) {
        log.info("📤 [RabbitMQ] Publishing Inscription → userId={} tournoiId={}",
                 event.getUserId(), event.getTournoiId());
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.EXCHANGE,
            RabbitMQConfig.RK_INSCRIPTION,
            event
        );
    }

    /**
     * Publie un event "statut changé" → permet à d'autres services de réagir
     * (ex: libérer le terrain si ANNULE, envoyer notifs si TERMINE, etc.)
     */
    public void publishStatutChanged(TournoiEvents.StatutChangedEvent event) {
        log.info("📤 [RabbitMQ] StatutChanged → {} : {} → {}",
                 event.getTournoiNom(), event.getAncienStatut(), event.getNouveauStatut());
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.EXCHANGE,
            RabbitMQConfig.RK_STATUT,
            event
        );
    }
}
