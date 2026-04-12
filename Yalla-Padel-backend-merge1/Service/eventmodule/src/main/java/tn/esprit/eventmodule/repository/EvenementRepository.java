package tn.esprit.eventmodule.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.eventmodule.entity.Evenement;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface EvenementRepository extends JpaRepository<Evenement, Long> {

    @Query("SELECT COUNT(e) > 0 FROM Evenement e " +
           "WHERE e.level = :level " +
           "AND e.startTime < :endTime " +
           "AND e.endTime > :startTime " +
           "AND (:eventId IS NULL OR e.eventId != :eventId)")
    boolean hasConflict(
            @Param("level") String level,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("eventId") Long eventId);
}
