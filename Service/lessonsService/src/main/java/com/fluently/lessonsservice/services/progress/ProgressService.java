package com.fluently.lessonsservice.services.progress;

import com.fluently.lessonsservice.dto.progress.LessonDifficultyDTO;
import com.fluently.lessonsservice.dto.progress.LessonDifficultyRowDTO;
import com.fluently.lessonsservice.entities.Lesson;
import com.fluently.lessonsservice.entities.LessonAttachment;
import com.fluently.lessonsservice.entities.progress.*;
import com.fluently.lessonsservice.dto.progress.CourseProgressSummaryDTO;
import com.fluently.lessonsservice.repositories.LessonAttachmentRepository;
import com.fluently.lessonsservice.repositories.LessonRepository;
import com.fluently.lessonsservice.repositories.progress.AttachmentProgressRepository;
import com.fluently.lessonsservice.repositories.progress.LessonProgressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProgressService implements IProgressService {

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private LessonAttachmentRepository lessonAttachmentRepository;

    @Autowired
    private LessonProgressRepository lessonProgressRepository;

    @Autowired
    private AttachmentProgressRepository attachmentProgressRepository;

    @Override
    public void startLesson(String learnerKey, Long lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new RuntimeException("Lesson not found with id: " + lessonId));

        Long courseId = lesson.getCourseId();

        LessonProgress lp = lessonProgressRepository
                .findByLearnerKeyAndLessonIdAndCourseId(learnerKey, lessonId, courseId)
                .orElse(null);

        if (lp == null) {
            lp = new LessonProgress();
            lp.setLearnerKey(learnerKey);
            lp.setLessonId(lessonId);
            lp.setCourseId(courseId);
            lp.setStatus(ProgressStatus.IN_PROGRESS);
            lp.setLastActivityAt(LocalDateTime.now());
            lessonProgressRepository.save(lp);
            return;
        }

        if (lp.getStatus() != ProgressStatus.COMPLETED) {
            lp.setStatus(ProgressStatus.IN_PROGRESS);
            lp.setLastActivityAt(LocalDateTime.now());
            lessonProgressRepository.save(lp);
        }
    }

    @Override
    public void completeLesson(String learnerKey, Long lessonId, Long timeSpentSeconds) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new RuntimeException("Lesson not found with id: " + lessonId));

        Long courseId = lesson.getCourseId();

        LessonProgress lp = lessonProgressRepository
                .findByLearnerKeyAndLessonIdAndCourseId(learnerKey, lessonId, courseId)
                .orElse(null);

        if (lp == null) {
            lp = new LessonProgress();
            lp.setLearnerKey(learnerKey);
            lp.setLessonId(lessonId);
            lp.setCourseId(courseId);
        }

        lp.setStatus(ProgressStatus.COMPLETED);
        lp.setCompletedAt(LocalDateTime.now());
        lp.setLastActivityAt(LocalDateTime.now());

        if (timeSpentSeconds != null && timeSpentSeconds >= 0) {
            Long old = lp.getTimeSpentSeconds() == null ? 0L : lp.getTimeSpentSeconds();
            lp.setTimeSpentSeconds(Math.max(old, timeSpentSeconds));
        }

        lessonProgressRepository.save(lp);
    }

    @Override
    public void completeAttachment(String learnerKey, Long attachmentId) {
        LessonAttachment att = lessonAttachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new RuntimeException("Attachment not found with id: " + attachmentId));

        AttachmentProgress existing = attachmentProgressRepository.findByLearnerKeyAndAttachmentId(learnerKey, attachmentId)
                .orElse(null);

        if (existing != null) {
            return; // already completed
        }

        Lesson lesson = lessonRepository.findById(att.getLessonId())
                .orElseThrow(() -> new RuntimeException("Lesson not found for attachment"));

        AttachmentProgress ap = new AttachmentProgress();
        ap.setLearnerKey(learnerKey);
        ap.setAttachmentId(attachmentId);
        ap.setLessonId(att.getLessonId());
        ap.setCourseId(lesson.getCourseId());
        ap.setCompletedAt(LocalDateTime.now());

        attachmentProgressRepository.save(ap);
    }

    @Override
    public CourseProgressSummaryDTO getCourseSummary(String learnerKey, Long courseId) {

        long totalLessons = lessonRepository.countByCourseId(courseId);
        long completedLessons = lessonProgressRepository.countByCourseAndLearnerAndStatus(courseId, learnerKey, ProgressStatus.COMPLETED);

        double percent = 0.0;
        if (totalLessons > 0) {
            percent = (completedLessons * 100.0) / totalLessons;
        }

        // find next lesson to unlock: smallest orderIndex NOT completed
        List<Long> completedLessonIds = lessonProgressRepository.findCompletedLessonIds(courseId, learnerKey);

        Lesson nextLesson = null;
        if (completedLessonIds == null || completedLessonIds.isEmpty()) {
            // no completed -> first lesson
            nextLesson = lessonRepository.findFirstByCourseIdOrderByOrderIndexAsc(courseId).orElse(null);
        } else {
            nextLesson = lessonRepository.findNextNotCompleted(courseId, completedLessonIds).orElse(null);
        }

        Integer unlockedMaxOrderIndex;
        Long nextLessonId;

        if (nextLesson == null) {
            // all completed -> unlock all
            unlockedMaxOrderIndex = lessonRepository.findMaxOrderIndexByCourse(courseId);
            nextLessonId = null;
        } else {
            unlockedMaxOrderIndex = nextLesson.getOrderIndex();
            nextLessonId = nextLesson.getId();
        }

        CourseProgressSummaryDTO dto = new CourseProgressSummaryDTO();
        dto.setCourseId(courseId);
        dto.setTotalLessons(totalLessons);
        dto.setCompletedLessons(completedLessons);
        dto.setCompletionPercent(Math.round(percent * 100.0) / 100.0);
        dto.setNextLessonId(nextLessonId);
        dto.setUnlockedOrderIndexMax(unlockedMaxOrderIndex == null ? 0 : unlockedMaxOrderIndex);

        return dto;
    }

    public void recordLessonHeartbeat(Long lessonId, String learnerKey, Long courseId, long deltaSeconds) {
        long safeDelta = Math.min(deltaSeconds, 60); // avoid crazy values

        LessonProgress lp = lessonProgressRepository
                .findByLearnerKeyAndLessonIdAndCourseId(learnerKey, lessonId, courseId)
                .orElseGet(() -> {
                    LessonProgress created = new LessonProgress();
                    created.setLearnerKey(learnerKey);
                    created.setLessonId(lessonId);
                    created.setCourseId(courseId);
                    created.setStatus(ProgressStatus.IN_PROGRESS);
                    created.setTimeSpentSeconds(0L);
                    created.setLastActivityAt(LocalDateTime.now());
                    return created;
                });

        if (lp.getStatus() == null || lp.getStatus() == ProgressStatus.NOT_STARTED) {
            lp.setStatus(ProgressStatus.IN_PROGRESS);
        }

        long current = lp.getTimeSpentSeconds() == null ? 0L : lp.getTimeSpentSeconds();
        lp.setTimeSpentSeconds(current + safeDelta);
        lp.setLastActivityAt(LocalDateTime.now());

        lessonProgressRepository.save(lp);
    }
    // =========================================================
    // ✅ NEW: Difficulty analytics (minimal, top 3 hard lessons)
    // =========================================================
    @Override
    public List<LessonDifficultyDTO> getLessonDifficultyByCourse(Long courseId) {

        List<LessonDifficultyRowDTO> rows = lessonProgressRepository.getDifficultyAggByCourse(courseId);

        // score = avgTimeSeconds * (1 - completionRate)
        // and only top 3 are HARD (as you requested: few results)
        var scored = rows.stream().map(r -> {
            double avg = (r.getAvgTimeSeconds() == null) ? 0.0 : r.getAvgTimeSeconds();
            long started = (r.getStartedCount() == null) ? 0L : r.getStartedCount();
            long completed = (r.getCompletedCount() == null) ? 0L : r.getCompletedCount();

            double rate = (started == 0) ? 0.0 : (completed * 1.0) / started;
            double score = avg * (1.0 - rate);

            return new Object() {
                LessonDifficultyRowDTO row = r;
                double completionRate = rate;
                double avgTime = avg;
                double s = score;
            };
        }).collect(Collectors.toList());

// sort by score descending
        List<Long> sortedByScore = scored.stream()
                .sorted(Comparator.comparingDouble(x -> -x.s))
                .map(x -> x.row.getLessonId())
                .collect(Collectors.toList());

        Set<Long> hardLessons = sortedByScore.stream().limit(2).collect(Collectors.toSet());
        Set<Long> easyLessons = sortedByScore.stream()
                .skip(Math.max(0, sortedByScore.size() - 2))
                .collect(Collectors.toSet());

        return scored.stream().map(x -> {

            String label;

            if (hardLessons.contains(x.row.getLessonId())) {
                label = "HARD";
            } else if (easyLessons.contains(x.row.getLessonId())) {
                label = "EASY";
            } else {
                label = "MEDIUM";
            }

            return new LessonDifficultyDTO(
                    x.row.getLessonId(),
                    x.row.getStartedCount(),
                    x.row.getCompletedCount(),
                    x.completionRate,
                    x.avgTime,
                    label
            );

        }).collect(Collectors.toList());
    }
}