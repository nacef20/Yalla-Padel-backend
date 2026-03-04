package com.fluently.lessonsservice.dto.progress;

import lombok.Data;

@Data
public class LessonStartRequest {
    private String learnerKey;
    private Long lessonId;

    public String getLearnerKey() { return learnerKey; }
    public void setLearnerKey(String learnerKey) { this.learnerKey = learnerKey; }

    public Long getLessonId() { return lessonId; }
    public void setLessonId(Long lessonId) { this.lessonId = lessonId; }
}