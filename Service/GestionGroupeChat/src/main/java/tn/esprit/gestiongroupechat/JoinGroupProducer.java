package tn.esprit.gestiongroupechat;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import tn.esprit.gestiongroupechat.entities.JoinGroup;
import tn.esprit.gestiongroupechat.entities.JoinGroupEvent;

@Service
public class JoinGroupProducer {

    private final RabbitTemplate rabbitTemplate;

    public JoinGroupProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendEvent( JoinGroupEvent event) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.JOIN_GROUP_QUEUE,
                event
        );
    }
}
