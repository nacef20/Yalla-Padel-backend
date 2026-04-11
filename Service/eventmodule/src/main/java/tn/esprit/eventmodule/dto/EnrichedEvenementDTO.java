package tn.esprit.eventmodule.dto;

import tn.esprit.eventmodule.entity.Evenement;

public class EnrichedEvenementDTO {
    private Evenement event;
    private UserDTO organizer;
    private String weatherInfo;

    public EnrichedEvenementDTO(Evenement event, UserDTO organizer, String weatherInfo) {
        this.event = event;
        this.organizer = organizer;
        this.weatherInfo = weatherInfo;
    }

    public Evenement getEvent() { return event; }
    public void setEvent(Evenement event) { this.event = event; }
    public UserDTO getOrganizer() { return organizer; }
    public void setOrganizer(UserDTO organizer) { this.organizer = organizer; }
    public String getWeatherInfo() { return weatherInfo; }
    public void setWeatherInfo(String weatherInfo) { this.weatherInfo = weatherInfo; }
}
