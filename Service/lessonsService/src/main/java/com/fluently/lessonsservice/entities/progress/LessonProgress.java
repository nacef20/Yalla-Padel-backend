package com.fluently.lessonsservice.entities.progress;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Table;
import jakarta.persistence.Entity;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "lesson_progress",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_learner_lesson_course",
                columnNames = {"learner_key", "lesson_id", "course_id"}
        )
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LessonProgress {

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public ProgressStatus getStatus() {
        return status;
    }

    public void setStatus(ProgressStatus status) {
        this.status = status;
    }

    public Long getTimeSpentSeconds() {
        return timeSpentSeconds;
    }

    public void setTimeSpentSeconds(Long timeSpentSeconds) {
        this.timeSpentSeconds = timeSpentSeconds;
    }

    public LocalDateTime getLastActivityAt() {
        return lastActivityAt;
    }

    public void setLastActivityAt(LocalDateTime lastActivityAt) {
        this.lastActivityAt = lastActivityAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="learner_key", nullable = false, length = 80)
    private String learnerKey;

    @Column(name="lesson_id", nullable = false)
    private Long lessonId;

    @Column(name="course_id", nullable = false)
    private Long courseId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ProgressStatus status;

    @Column(name="time_spent_seconds")
    private Long timeSpentSeconds;

    @Column(name="last_activity_at")
    private LocalDateTime lastActivityAt;

    @Column(name="completed_at")
    private LocalDateTime completedAt;
}