package tn.esprit.userservice.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import tn.esprit.userservice.config.RabbitMQConfig;
import tn.esprit.userservice.dto.EventRegistrationMessage;

@Service
public class ParticipationConsumer {

    private static final Logger log = LoggerFactory.getLogger(ParticipationConsumer.class);

    /**
     * This method listens to the RabbitMQ queue asynchronously.
     * Whenever eventmodule sends a message about a new registration, 
     * this method is triggered automatically without blocking eventmodule.
     */
    @RabbitListener(queues = RabbitMQConfig.REGISTRATION_QUEUE)
    public void consumeRegistrationMessage(EventRegistrationMessage message) {
        log.info("==================================================");
        log.info("🐇 [RABBITMQ ASYNC] Received Event Registration!");
        log.info("User ID: {}", message.getUserId());
        log.info("Event ID: {} - Title: {}", message.getEventTitle());
        log.info("Participation ID: {}", message.getParticipationId());
        log.info("Action: Sending confirmation email and updating rewards points...");
        log.info("==================================================");
    }
}
