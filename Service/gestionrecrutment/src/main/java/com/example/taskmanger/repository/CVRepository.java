package com.example.taskmanger.repository;

import com.example.taskmanger.model.CV;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CVRepository extends JpaRepository<CV, Long> {
    List<CV> findByCandidateId(Integer candidateId);
} 