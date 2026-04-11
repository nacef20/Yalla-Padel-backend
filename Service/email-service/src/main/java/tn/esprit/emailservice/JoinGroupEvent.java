package tn.esprit.emailservice;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class JoinGroupEvent {

    private Long groupId;
    private String groupName;
    private String userId;
    private String adminEmail;
}
