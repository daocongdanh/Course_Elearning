package group.project.cursusonlinecoursemanagement.course.repository;

import group.project.cursusonlinecoursemanagement.course.domain.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    //Method to check if a comment with a certain lessonId exists
    @Query("SELECT CASE " +
            "WHEN COUNT(c) > 0 " +
            "THEN TRUE ELSE FALSE END " +
            "FROM Comment c " +
            "WHERE c.lesson.lessonId = :lessonId")
    boolean existsByLessonId(@Param("lessonId") UUID lessonId);
}
