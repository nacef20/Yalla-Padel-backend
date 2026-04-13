package tn.esprit.emailservice;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TournoiGagnantEvent {

    private Long tournoiId;
    private String tournoiNom;
    private String userId;
    private String username;
    private String firstName;
    private String lastName;
    private String userEmail;
}