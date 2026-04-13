package tn.esprit.emailservice;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class TournoiInscriptionConsumer {

    private final EmailService emailService;

    public TournoiInscriptionConsumer(EmailService emailService) {
        this.emailService = emailService;
    }

    @RabbitListener(queues = RabbitMQConfig.TOURNOI_INSCRIPTION_QUEUE)
    public void receive(TournoiInscriptionEvent event) {
        emailService.sendEmail(event);
    }
}