package tn.esprit.eventmodule.producer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import tn.esprit.eventmodule.config.RabbitMQConfig;
import tn.esprit.eventmodule.dto.EventRegistrationMessage;

@Service
public class ParticipationProducer {

    private static final Logger log = LoggerFactory.getLogger(ParticipationProducer.class);

    private final RabbitTemplate rabbitTemplate;

    public ParticipationProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendRegistrationNotification(EventRegistrationMessage message) {
        log.info("Sending registration notification to RabbitMQ for Participation ID: {}", message.getParticipationId());
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.REGISTRATION_ROUTING_KEY,
                message
        );
        log.info("Message sent successfully!");
    }
}
