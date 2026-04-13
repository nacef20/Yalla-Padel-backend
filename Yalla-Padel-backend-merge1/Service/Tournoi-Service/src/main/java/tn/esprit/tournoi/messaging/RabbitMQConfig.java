package tn.esprit.tournoi.messaging;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ──────────────────────────────────────────────────────────────────
 * SCÉNARIO 2 : Communication asynchrone via RabbitMQ
 *
 * EXCHANGES / QUEUES déclarés :
 *
 *  tournoi.exchange  (Direct Exchange)
 *    ├── tournoi.created.queue    → notifié quand un tournoi est créé
 *    │   binding key : tournoi.created
 *    ├── tournoi.inscription.queue → notifié quand un joueur s'inscrit
 *    │   binding key : tournoi.inscription
 *    └── tournoi.statut.queue     → notifié quand le statut change
 *        binding key : tournoi.statut
 *
 * forum-service ou notification-service consomment ces queues.
 * ──────────────────────────────────────────────────────────────────
 */
@Configuration
public class RabbitMQConfig {

    // ── Exchange ──────────────────────────────────────────────────
    public static final String EXCHANGE = "tournoi.exchange";

    // ── Routing keys ─────────────────────────────────────────────
    public static final String RK_CREATED     = "tournoi.created";
    public static final String RK_INSCRIPTION = "tournoi.inscription";
    public static final String RK_STATUT      = "tournoi.statut";
    public static final String RK_GAGNANT     = "tournoi.gagnant";

    // ── Queue names ───────────────────────────────────────────────
    public static final String Q_CREATED      = "tournoi.created.queue";
    public static final String Q_INSCRIPTION  = "tournoi.inscription.queue";
    public static final String Q_STATUT       = "tournoi.statut.queue";
    public static final String Q_GAGNANT      = "tournoi.gagnant.queue";

    // ── Beans ─────────────────────────────────────────────────────

    @Bean
    DirectExchange tournoiExchange() {
        return new DirectExchange(EXCHANGE, true, false);
    }

    @Bean Queue queueCreated()     { return new Queue(Q_CREATED,     true); }
    @Bean Queue queueInscription() { return new Queue(Q_INSCRIPTION, true); }
    @Bean Queue queueStatut()      { return new Queue(Q_STATUT,      true); }
    @Bean Queue queueGagnant()     { return new Queue(Q_GAGNANT,     true); }

    @Bean Binding bindingCreated(Queue queueCreated, DirectExchange tournoiExchange) {
        return BindingBuilder.bind(queueCreated).to(tournoiExchange).with(RK_CREATED);
    }

    @Bean Binding bindingInscription(Queue queueInscription, DirectExchange tournoiExchange) {
        return BindingBuilder.bind(queueInscription).to(tournoiExchange).with(RK_INSCRIPTION);
    }

    @Bean Binding bindingStatut(Queue queueStatut, DirectExchange tournoiExchange) {
        return BindingBuilder.bind(queueStatut).to(tournoiExchange).with(RK_STATUT);
    }

    @Bean Binding bindingGagnant(Queue queueGagnant, DirectExchange tournoiExchange) {
        return BindingBuilder.bind(queueGagnant).to(tournoiExchange).with(RK_GAGNANT);
    }

    /** Convertisseur JSON — sérialise les objets Java en JSON dans les messages */
    @Bean
    public Jackson2JsonMessageConverter jsonConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /** RabbitTemplate avec convertisseur JSON */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory cf) {
        RabbitTemplate template = new RabbitTemplate(cf);
        template.setMessageConverter(jsonConverter());
        return template;
    }
}
