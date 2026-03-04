package com.fluently.lessonsservice.repositories;

import com.fluently.lessonsservice.entities.AttachmentType;
import com.fluently.lessonsservice.entities.LessonAttachment;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LessonAttachmentRepository extends JpaRepository<LessonAttachment, Long> {

    // List attachments of a lesson sorted by upload date
    List<LessonAttachment> findByLessonIdOrderByUploadDateDesc(Long lessonId);

    // Useful later for analytics / unlocking rules
    @Query("select count(a) from LessonAttachment a where a.lessonId = :lessonId and a.type = :type")
    long countByLessonIdAndType(@Param("lessonId") Long lessonId, @Param("type") AttachmentType type);

    // Example "smart query" (counts by type for a lesson)
    @Query("select a.type, count(a) from LessonAttachment a where a.lessonId = :lessonId group by a.type")
    List<Object[]> countAttachmentsByType(@Param("lessonId") Long lessonId);
}