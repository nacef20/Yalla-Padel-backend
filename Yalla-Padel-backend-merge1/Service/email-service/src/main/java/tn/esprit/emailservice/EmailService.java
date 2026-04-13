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

    public void sendEmail(TournoiInscriptionEvent event) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(event.getUserEmail());
        message.setSubject("Inscription au tournoi confirmée");

        message.setText(
                "Bonjour, votre inscription au tournoi '" + event.getTournoiNom() +
                        "' a bien été enregistrée.\n" +
                        "Nombre actuel de participants: " + event.getNbParticipantsActuel() +
                        "/" + event.getCapaciteMax()
        );

        mailSender.send(message);

        System.out.println("TOURNOI EMAIL SENT SUCCESSFULLY");
    }

    public void sendEmail(TournoiGagnantEvent event) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(event.getUserEmail());
        message.setSubject("Vous êtes le gagnant du tournoi");

        String displayName = (event.getFirstName() != null && !event.getFirstName().isBlank())
                ? event.getFirstName()
                : event.getUsername();

        message.setText(
                "Bonjour " + displayName + ",\n" +
                        "Félicitations, vous avez été désigné gagnant du tournoi '" + event.getTournoiNom() + "'.\n" +
                        "Tournoi ID: " + event.getTournoiId()
        );

        mailSender.send(message);

        System.out.println("TOURNOI WINNER EMAIL SENT SUCCESSFULLY");
    }
}
