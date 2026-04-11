package tn.esprit.eventmodule.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

public class EventRegistrationMessage implements Serializable {
    private Long participationId;
    private String userId;
    private Long eventId;
    private String eventTitle;
    private LocalDateTime registrationDate;

    public EventRegistrationMessage() {
    }

    public EventRegistrationMessage(Long participationId, String userId, Long eventId, String eventTitle, LocalDateTime registrationDate) {
        this.participationId = participationId;
        this.userId = userId;
        this.eventId = eventId;
        this.eventTitle = eventTitle;
        this.registrationDate = registrationDate;
    }

    public Long getParticipationId() { return participationId; }
    public void setParticipationId(Long participationId) { this.participationId = participationId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public Long getEventId() { return eventId; }
    public void setEventId(Long eventId) { this.eventId = eventId; }

    public String getEventTitle() { return eventTitle; }
    public void setEventTitle(String eventTitle) { this.eventTitle = eventTitle; }

    public LocalDateTime getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(LocalDateTime registrationDate) { this.registrationDate = registrationDate; }
}
