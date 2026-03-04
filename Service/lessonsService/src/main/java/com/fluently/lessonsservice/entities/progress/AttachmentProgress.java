package com.fluently.lessonsservice.entities.progress;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "attachment_progress",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_attachment_progress_learner_attachment", columnNames = {"learner_key", "attachment_id"})
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttachmentProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="learner_key", nullable = false, length = 80)
    private String learnerKey;

    @Column(name="attachment_id", nullable = false)
    private Long attachmentId;

    @Column(name="lesson_id", nullable = false)
    private Long lessonId;

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

    public Long getAttachmentId() {
        return attachmentId;
    }

    public void setAttachmentId(Long attachmentId) {
        this.attachmentId = attachmentId;
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

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    @Column(name="course_id", nullable = false)
    private Long courseId;

    @Column(name="completed_at", nullable = false)
    private LocalDateTime completedAt;
}
