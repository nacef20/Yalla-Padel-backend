package tn.esprit.eventmodule.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.eventmodule.entity.Evenement;
import tn.esprit.eventmodule.repository.EvenementRepository;
import tn.esprit.eventmodule.repository.ParticipationRepository;
import tn.esprit.eventmodule.exception.EventConflictException;

import java.util.List;

import tn.esprit.eventmodule.client.UserClient;
import tn.esprit.eventmodule.client.WeatherClient;
import tn.esprit.eventmodule.dto.EnrichedEvenementDTO;
import tn.esprit.eventmodule.dto.UserDTO;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
public class EvenementService {

    private final EvenementRepository evenementRepository;
    private final ParticipationRepository participationRepository;
    private final ImageStorageService imageStorageService;
    private final UserClient userClient;
    private final WeatherClient weatherClient;

    public EvenementService(EvenementRepository evenementRepository, ParticipationRepository participationRepository,
            ImageStorageService imageStorageService, UserClient userClient, WeatherClient weatherClient) {
        this.evenementRepository = evenementRepository;
        this.participationRepository = participationRepository;
        this.imageStorageService = imageStorageService;
        this.userClient = userClient;
        this.weatherClient = weatherClient;
    }

    @Transactional
    public Evenement create(Evenement evenement) {
        if (evenementRepository.hasConflict(evenement.getLevel(), evenement.getStartTime(), evenement.getEndTime(),
                null)) {
            throw new EventConflictException(
                    "An event with level " + evenement.getLevel() + " already exists in the given time frame.");
        }
        evenement.setImageUrl(imageStorageService.shrinkIfNeeded(evenement.getImageUrl()));
        return evenementRepository.save(evenement);
    }

    public List<Evenement> findAll() {
        return evenementRepository.findAll();
    }

    public Evenement findById(Long id) {
        return evenementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evenement not found with id: " + id));
    }

    // --- OpenFeign Synchronous Communication ---

    public List<EnrichedEvenementDTO> findAllEnriched() {
        return findAll().stream()
                .map(this::enrichEvenement)
                .toList();
    }

    public EnrichedEvenementDTO findEnrichedById(Long id) {
        Evenement event = findById(id);
        return enrichEvenement(event);
    }

    private EnrichedEvenementDTO enrichEvenement(Evenement event) {
        UserDTO organizer = null;
        try {
            // Synchronous call to userservice
            organizer = userClient.getUserById(event.getOrganizerId());
        } catch (Exception e) {
            // Fallback if userservice is down or user not found
            organizer = new UserDTO();
            organizer.setId(event.getOrganizerId());
            organizer.setUsername("Unknown (Service Unavailable)");
        }

        String weatherInfo = "Weather info currently unavailable.";
        
        if (event.getStartTime() != null) {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime eventStart = event.getStartTime();
            long daysBetween = ChronoUnit.DAYS.between(now.toLocalDate(), eventStart.toLocalDate());

            if (daysBetween < 0) {
                weatherInfo = "Event already passed.";
            } else if (daysBetween > 14) {
                weatherInfo = "Weather forecast not available yet (too far in the future).";
            } else {
                try {
                    String dateStr = eventStart.toLocalDate().toString(); // "YYYY-MM-DD"
                    // Synchronous call to external Open-Meteo API for specific date
                    var weatherResponse = weatherClient.getDailyWeather(
                            36.8065, 10.1815, "weathercode,temperature_2m_max", "auto", dateStr, dateStr
                    );
                    
                    if (weatherResponse.getDaily() != null && weatherResponse.getDaily().getWeathercode() != null && !weatherResponse.getDaily().getWeathercode().isEmpty()) {
                        int code = weatherResponse.getDaily().getWeathercode().get(0);
                        double maxTemp = weatherResponse.getDaily().getTemperature_2m_max().get(0);
                        
                        // WMO standard weathercodes: >= 51 means rain/snow/storms
                        if (code >= 51) {
                            weatherInfo = "⚠️ Warning: Bad weather expected (Rain/Storms). Max Temperature: " + maxTemp + "°C. Consider playing indoors.";
                        } else {
                            weatherInfo = "☀️ Ideal weather for outdoor matches! Max Temperature: " + maxTemp + "°C.";
                        }
                    }
                } catch (Exception e) {
                    weatherInfo = "Weather API unreachable.";
                }
            }
        }

        return new EnrichedEvenementDTO(event, organizer, weatherInfo);
    }

    @Transactional
    public Evenement update(Long id, Evenement evenement) {
        if (evenementRepository.hasConflict(evenement.getLevel(), evenement.getStartTime(), evenement.getEndTime(),
                id)) {
            throw new EventConflictException(
                    "An event with level " + evenement.getLevel() + " already exists in the given time frame.");
        }

        Evenement existing = findById(id);
        existing.setTitle(evenement.getTitle());
        existing.setDescription(evenement.getDescription());
        existing.setLevel(evenement.getLevel());
        existing.setStartTime(evenement.getStartTime());
        existing.setEndTime(evenement.getEndTime());
        existing.setLocation(evenement.getLocation());
        existing.setCapacity(evenement.getCapacity());
        existing.setReminderSent(evenement.isReminderSent());
        existing.setImageUrl(imageStorageService.shrinkIfNeeded(evenement.getImageUrl()));
        if (evenement.getOrganizerId() != null) {
            existing.setOrganizerId(evenement.getOrganizerId());
        }
        return evenementRepository.save(existing);
    }

    @Transactional
    public void deleteById(Long id) {
        if (!evenementRepository.existsById(id)) {
            throw new RuntimeException("Evenement not found with id: " + id);
        }
        evenementRepository.deleteById(id);
    }

    public int getAvailableSeats(Long eventId) {
        Evenement event = findById(eventId);
        long participants = participationRepository.countByEvent_EventId(eventId);
        return Math.max(0, event.getCapacity() - (int) participants);
    }

    public boolean isFull(Long eventId) {
        return getAvailableSeats(eventId) == 0;
    }
}
