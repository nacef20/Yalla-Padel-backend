package com.example.taskmanger.controller;

import com.example.taskmanger.model.CV;
import com.example.taskmanger.model.CVMatch;
import com.example.taskmanger.repository.CVMatchRepository;
import com.example.taskmanger.service.CVService;
import com.example.taskmanger.model.User;
import com.example.taskmanger.repository.CVRepository;
import com.example.taskmanger.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/cvs")
public class CVController {

    @Autowired
    private CVService cvService;
    @Autowired
    private CVRepository cvRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CVMatchRepository cvMatchRepository;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadCV(@RequestParam("file") MultipartFile file,
            @RequestParam("candidateId") Integer candidateId,
            @RequestParam("jobOfferId") Long jobOfferId) {
        try {
            User candidate = userRepository.findById(candidateId).orElse(null);
            if (candidate == null) {
                return ResponseEntity.badRequest().body("Candidate not found.");
            }

            /*
             * // Check if already uploaded for this job offer using optimized query
             * List<Long> existingJobIds =
             * cvMatchRepository.findJobOfferIdsByCandidateId(candidateId);
             * if (existingJobIds.contains(jobOfferId)) {
             * return ResponseEntity.status(409).
             * body("You have already uploaded your CV for this job offer.");
             * }
             */

            // Use the new method to get both the CV and extracted attributes for a specific
            // job offer
            var result = cvService.processCVWithAttributesForJob(file.getBytes(), candidate, jobOfferId);
            return ResponseEntity.ok(result);
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("File upload error.");
        }
    }

    @GetMapping("/matches")
    public ResponseEntity<List<CVMatch>> getCVMatches(@RequestParam Long cvId) {
        CV cv = cvRepository.findById(cvId).orElse(null);
        if (cv == null) {
            return ResponseEntity.notFound().build();
        }
        List<CVMatch> matches = cvService.matchCVWithAllJobs(cv);
        return ResponseEntity.ok(matches);
    }

    @GetMapping("/matches/by-candidate")
    public ResponseEntity<List<CVMatch>> getLatestMatchesByCandidate(@RequestParam Integer candidateId) {
        List<CVMatch> matches = cvMatchRepository.findByCandidateIdWithFetch(candidateId);

        // Auto-sync stale scores (fix data from 0-1 range to 0-100 range)
        boolean needsUpdate = false;
        for (CVMatch match : matches) {
            double currentScore = match.getMatchScore();
            double freshScore = cvService.recalculateScore(match);
            if (Math.abs(currentScore - freshScore) > 0.01) {
                match.setMatchScore(freshScore);
                needsUpdate = true;
            }
        }
        if (needsUpdate) {
            cvMatchRepository.saveAll(matches);
        }

        // Filter to get only the latest match per job
        Map<Long, CVMatch> latestMatchByJob = new HashMap<>();
        for (CVMatch match : matches) {
            Long jobId = match.getJobOffer().getId();
            if (!latestMatchByJob.containsKey(jobId) ||
                    match.getMatchDate().after(latestMatchByJob.get(jobId).getMatchDate())) {
                latestMatchByJob.put(jobId, match);
            }
        }
        return ResponseEntity.ok(new ArrayList<>(latestMatchByJob.values()));
    }

    @GetMapping("/uploaded-job-ids")
    public ResponseEntity<List<Long>> getUploadedJobIdsByCandidate(@RequestParam Integer candidateId) {
        List<Long> jobOfferIds = cvMatchRepository.findJobOfferIdsByCandidateId(candidateId);
        return ResponseEntity.ok(jobOfferIds);
    }

    @GetMapping("/candidates/{id}")
    public ResponseEntity<List<CV>> getCandidateCVs(@PathVariable Long id) {
        // TODO: Get CVs for candidate
        return ResponseEntity.ok(List.of()); // Placeholder
    }
}
