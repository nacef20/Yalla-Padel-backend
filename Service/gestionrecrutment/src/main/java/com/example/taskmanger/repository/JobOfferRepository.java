package com.example.taskmanger.repository;

import com.example.taskmanger.model.JobOffer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobOfferRepository extends JpaRepository<JobOffer, Long> {
} 