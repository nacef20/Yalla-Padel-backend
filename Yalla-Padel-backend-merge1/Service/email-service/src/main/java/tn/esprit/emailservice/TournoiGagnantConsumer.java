package tn.esprit.emailservice;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class TournoiGagnantConsumer {

    private final EmailService emailService;

    public TournoiGagnantConsumer(EmailService emailService) {
        this.emailService = emailService;
    }

    @RabbitListener(queues = RabbitMQConfig.TOURNOI_GAGNANT_QUEUE)
    public void receive(TournoiGagnantEvent event) {
        emailService.sendEmail(event);
    }
}