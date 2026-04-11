package tn.esprit.emailservice;

import jakarta.annotation.PostConstruct;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }



    public void sendEmail(JoinGroupEvent event) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(event.getAdminEmail());
        message.setSubject("New Join Group Request");

        message.setText(
                "User " + event.getUserId() +
                        " wants to join group " + event.getGroupName()
        );

        mailSender.send(message);

        System.out.println("EMAIL SENT SUCCESSFULLY");
    }
}
