package tn.esprit.eventmodule.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.eventmodule.entity.Participation;

public interface ParticipationRepository extends JpaRepository<Participation, Long> {

    long countByEvent_EventId(Long eventId);
}
