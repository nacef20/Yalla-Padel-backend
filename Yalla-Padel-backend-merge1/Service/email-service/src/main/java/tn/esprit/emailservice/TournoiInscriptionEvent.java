package tn.esprit.emailservice;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TournoiInscriptionEvent {

    private Long tournoiId;
    private String tournoiNom;
    private String userId;
    private String userEmail;
    private int nbParticipantsActuel;
    private int capaciteMax;
}