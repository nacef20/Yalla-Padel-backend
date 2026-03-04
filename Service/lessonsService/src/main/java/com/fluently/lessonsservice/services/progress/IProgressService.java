package com.fluently.lessonsservice.services.progress;

import com.fluently.lessonsservice.dto.progress.CourseProgressSummaryDTO;
import com.fluently.lessonsservice.dto.progress.LessonDifficultyDTO;

import java.util.List;

public interface IProgressService {

    void startLesson(String learnerKey, Long lessonId);

    void completeLesson(String learnerKey, Long lessonId, Long timeSpentSeconds);

    void completeAttachment(String learnerKey, Long attachmentId);

    CourseProgressSummaryDTO getCourseSummary(String learnerKey, Long courseId);

    void recordLessonHeartbeat(Long lessonId, String learnerKey, Long courseId, long deltaSeconds);

    List<LessonDifficultyDTO> getLessonDifficultyByCourse(Long courseId);
}
