package com.fluently.lessonsservice.dto.progress;

import lombok.Data;

@Data
public class LessonCompleteRequest {
    public String getLearnerKey() {
        return learnerKey;
    }

    public void setLearnerKey(String learnerKey) {
        this.learnerKey = learnerKey;
    }

    public Long getLessonId() {
        return lessonId;
    }

    public void setLessonId(Long lessonId) {
        this.lessonId = lessonId;
    }

    public Long getTimeSpentSeconds() {
        return timeSpentSeconds;
    }

    public void setTimeSpentSeconds(Long timeSpentSeconds) {
        this.timeSpentSeconds = timeSpentSeconds;
    }

    private String learnerKey;
    private Long lessonId;
    private Long timeSpentSeconds; // optional
}
