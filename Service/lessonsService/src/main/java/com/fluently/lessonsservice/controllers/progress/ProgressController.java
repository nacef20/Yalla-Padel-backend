package com.fluently.lessonsservice.controllers.progress;

import com.fluently.lessonsservice.dto.progress.*;
import com.fluently.lessonsservice.services.progress.IProgressService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lessons/progress")
public class ProgressController {

    @Autowired
    private IProgressService progressService;

    @PostMapping("/lesson/start")
    public ResponseEntity<Void> startLesson(@RequestBody LessonStartRequest req) {
        progressService.startLesson(req.getLearnerKey(), req.getLessonId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/lesson/complete")
    public ResponseEntity<Void> completeLesson(@RequestBody LessonCompleteRequest req) {
        progressService.completeLesson(req.getLearnerKey(), req.getLessonId(), req.getTimeSpentSeconds());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/attachment/complete")
    public ResponseEntity<Void> completeAttachment(@RequestBody AttachmentCompleteRequest req) {
        progressService.completeAttachment(req.getLearnerKey(), req.getAttachmentId());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/course/{courseId}/summary")
    public ResponseEntity<CourseProgressSummaryDTO> summary(@PathVariable Long courseId,
                                                            @RequestParam String learnerKey) {
        return ResponseEntity.ok(progressService.getCourseSummary(learnerKey, courseId));
    }

    @PostMapping("/lessons/{lessonId}/heartbeat")
    public ResponseEntity<Void> heartbeat(
            @PathVariable Long lessonId,
            @Valid @RequestBody LessonHeartbeatRequest request
    ) {
        progressService.recordLessonHeartbeat(
                lessonId,
                request.getLearnerKey(),
                request.getCourseId(),
                request.getDeltaSeconds()
        );
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/course/{courseId}/difficulty")
    public ResponseEntity<List<LessonDifficultyDTO>> difficulty(@PathVariable Long courseId) {
        return ResponseEntity.ok(progressService.getLessonDifficultyByCourse(courseId));
    }
}
