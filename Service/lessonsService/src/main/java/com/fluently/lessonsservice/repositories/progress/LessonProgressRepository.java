package com.fluently.lessonsservice.repositories.progress;

import com.fluently.lessonsservice.dto.progress.LessonDifficultyRowDTO;
import com.fluently.lessonsservice.entities.progress.LessonProgress;
import com.fluently.lessonsservice.entities.progress.ProgressStatus;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface LessonProgressRepository extends JpaRepository<LessonProgress, Long> {

    //Optional<LessonProgress> findByLearnerKeyAndLessonId(String learnerKey, Long lessonId);
    Optional<LessonProgress> findByLearnerKeyAndLessonIdAndCourseId(String learnerKey, Long lessonId, Long courseId);

    @Query("select count(lp) from LessonProgress lp " +
            "where lp.courseId = :courseId and lp.learnerKey = :learnerKey and lp.status = :status")
    long countByCourseAndLearnerAndStatus(@Param("courseId") Long courseId,
                                          @Param("learnerKey") String learnerKey,
                                          @Param("status") ProgressStatus status);

    @Query("select lp.lessonId from LessonProgress lp " +
            "where lp.courseId = :courseId and lp.learnerKey = :learnerKey and lp.status = com.fluently.lessonsservice.entities.progress.ProgressStatus.COMPLETED")
    List<Long> findCompletedLessonIds(@Param("courseId") Long courseId,
                                      @Param("learnerKey") String learnerKey);

    // 🔥 "stuck" analytics later
    @Query("select lp from LessonProgress lp " +
            "where lp.courseId = :courseId and lp.learnerKey = :learnerKey and lp.status <> com.fluently.lessonsservice.entities.progress.ProgressStatus.COMPLETED " +
            "order by lp.lastActivityAt asc")
    List<LessonProgress> findNonCompletedOrdered(@Param("courseId") Long courseId,

                                                 @Param("learnerKey") String learnerKey);


    @Query("""
    SELECT new com.fluently.lessonsservice.dto.progress.LessonDifficultyRowDTO(
        lp.lessonId,
        COUNT(DISTINCT lp.learnerKey),
        COUNT(DISTINCT CASE WHEN lp.completedAt IS NOT NULL THEN lp.learnerKey ELSE NULL END),
        AVG(lp.timeSpentSeconds)
    )
    FROM LessonProgress lp
    WHERE lp.courseId = :courseId
    GROUP BY lp.lessonId
""")
    List<LessonDifficultyRowDTO> getDifficultyAggByCourse(@Param("courseId") Long courseId);
}
