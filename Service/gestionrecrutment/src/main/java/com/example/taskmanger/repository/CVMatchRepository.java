package com.example.taskmanger.repository;

import com.example.taskmanger.model.CVMatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CVMatchRepository extends JpaRepository<CVMatch, Long> {

    @Query("SELECT DISTINCT m.jobOffer.id FROM CVMatch m WHERE m.cv.candidate.id = :candidateId")
    List<Long> findJobOfferIdsByCandidateId(@Param("candidateId") Integer candidateId);

    @Query("SELECT m FROM CVMatch m JOIN FETCH m.cv JOIN FETCH m.jobOffer WHERE m.cv.candidate.id = :candidateId")
    List<CVMatch> findByCandidateIdWithFetch(@Param("candidateId") Integer candidateId);

    List<CVMatch> findByStatus(String status);

    List<CVMatch> findByJobOfferId(Long jobOfferId);

    @Modifying
    @Transactional
    void deleteByJobOfferId(Long jobOfferId);
}