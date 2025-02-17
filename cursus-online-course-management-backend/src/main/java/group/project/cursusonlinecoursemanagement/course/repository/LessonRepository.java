package group.project.cursusonlinecoursemanagement.course.repository;

import group.project.cursusonlinecoursemanagement.course.domain.entity.Lesson;
import group.project.cursusonlinecoursemanagement.course.domain.entity.LessonStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface LessonRepository extends JpaRepository<Lesson, UUID> {
    List<Lesson> findByCourse_CourseId(UUID courseId);

    @Query("SELECT COUNT(*) " +
            "FROM Lesson l " +
            "WHERE l.course.courseId= :courseId " +
            "AND l.isPublished = true")
    int countLessonWithPublicStatus(UUID courseId);

    boolean existsByLessonProgressList_Lesson_LessonIdAndLessonProgressList_User_Email(UUID lessonId, String email);

    boolean existsByLessonProgressList_Lesson_LessonIdAndLessonProgressList_User_EmailAndLessonProgressList_Status(UUID lessonId, String email, LessonStatus status);

    @Modifying
    @Transactional
    @Query("DELETE FROM Lesson l WHERE l.lessonId = :lessonId")
    void deleteByLessonId(@Param("lessonId") UUID lessonId);
}
