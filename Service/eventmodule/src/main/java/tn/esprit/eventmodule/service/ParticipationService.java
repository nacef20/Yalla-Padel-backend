package tn.esprit.eventmodule.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.eventmodule.entity.Evenement;
import tn.esprit.eventmodule.entity.Participation;
import tn.esprit.eventmodule.repository.ParticipationRepository;

import java.time.LocalDateTime;
import java.util.List;

import tn.esprit.eventmodule.producer.ParticipationProducer;
import tn.esprit.eventmodule.dto.EventRegistrationMessage;

@Service
public class ParticipationService {

    private final ParticipationRepository participationRepository;
    private final EvenementService evenementService;
    private final ParticipationProducer participationProducer;

    public ParticipationService(ParticipationRepository participationRepository, EvenementService evenementService, ParticipationProducer participationProducer) {
        this.participationRepository = participationRepository;
        this.evenementService = evenementService;
        this.participationProducer = participationProducer;
    }

    @Transactional
    public Participation create(Participation participation) {
        if (participation.getEvent() != null && participation.getEvent().getEventId() != null) {
            participation.setEvent(evenementService.findById(participation.getEvent().getEventId()));
        }
        return participationRepository.save(participation);
    }

    public List<Participation> findAll() {
        return participationRepository.findAll();
    }

    public Participation findById(Long id) {
        return participationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Participation not found with id: " + id));
    }

    @Transactional
    public Participation update(Long id, Participation participation) {
        Participation existing = findById(id);
        if (participation.getUserId() != null && !participation.getUserId().isBlank()) {
            existing.setUserId(participation.getUserId());
        }
        if (participation.getEvent() != null && participation.getEvent().getEventId() != null) {
            existing.setEvent(evenementService.findById(participation.getEvent().getEventId()));
        }
        if (participation.getCheckInTime() != null) {
            existing.setCheckInTime(participation.getCheckInTime());
        }
        return participationRepository.save(existing);
    }

    @Transactional
    public void deleteById(Long id) {
        if (!participationRepository.existsById(id)) {
            throw new RuntimeException("Participation not found with id: " + id);
        }
        participationRepository.deleteById(id);
    }

    @Transactional
    public Participation registerToEvent(String userId, Long eventId) {
        Evenement event = evenementService.findById(eventId);
        if (evenementService.isFull(eventId)) {
            throw new RuntimeException("Event is full");
        }
        Participation participation = new Participation();
        participation.setUserId(userId);
        participation.setEvent(event);
        participation.setRegistrationDate(LocalDateTime.now());
        
        Participation savedParticipation = participationRepository.save(participation);

        // 🐇 Asynchronous Communication: Send message to RabbitMQ
        EventRegistrationMessage message = new EventRegistrationMessage(
                savedParticipation.getParticipantId(),
                userId,
                eventId,
                event.getTitle(),
                savedParticipation.getRegistrationDate()
        );
        participationProducer.sendRegistrationNotification(message);

        return savedParticipation;
    }

    @Transactional
    public void unregisterFromEvent(Long participationId) {
        deleteById(participationId);
    }

    @Transactional
    public Participation checkIn(Long participationId) {
        Participation p = findById(participationId);
        p.setCheckInTime(LocalDateTime.now());
        return participationRepository.save(p);
    }
}
