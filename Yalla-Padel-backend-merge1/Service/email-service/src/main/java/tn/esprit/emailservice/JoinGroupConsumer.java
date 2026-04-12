package tn.esprit.emailservice;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class JoinGroupConsumer {

    private final EmailService emailService;

    public JoinGroupConsumer(EmailService emailService) {
        this.emailService = emailService;
    }

    @RabbitListener(queues = RabbitMQConfig.JOIN_GROUP_QUEUE)
    public void receive(JoinGroupEvent event) {

        System.out.println("🔥 EMAIL TO ADMIN: " + event.getAdminEmail());

        emailService.sendEmail(event);
    }
}