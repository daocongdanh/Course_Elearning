package group.project.cursusonlinecoursemanagement.course.repository;


import group.project.cursusonlinecoursemanagement.course.domain.entity.LessonStatus;
import group.project.cursusonlinecoursemanagement.course.domain.entity.UserLessonProgress;
import group.project.cursusonlinecoursemanagement.course.domain.entity.UserLessonProgressId;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserLessonProgressRepository extends JpaRepository<UserLessonProgress, UserLessonProgressId> {


    @Transactional
    @Modifying
    @Query(value="INSERT INTO user_lesson_progress (lesson_id, user_id, status)\n" +
            "SELECT l.lesson_id, u.user_id, \n" +
            "  CASE \n" +
            "    WHEN ROW_NUMBER() OVER (PARTITION BY u.user_id ORDER BY l.lesson_id) = 1 THEN 'IN_PROGRESS'\n" +
            "    ELSE 'NOT_STARTED'\n" +
            "  END AS status\n" +
            "FROM lesson l\n" +
            "JOIN course c ON l.course_id = c.course_id\n" +
            "JOIN \"user\" u ON u.email = :email \n" +
            "WHERE c.course_id = :courseId",nativeQuery = true)
    int startCourse(UUID courseId,String email);

    @Transactional
    @Modifying
    @Query(value="INSERT INTO user_lesson_progress (lesson_id, user_id, status)\n" +
            "SELECT l.lesson_id, u.user_id, 'COMPLETED'\n" +
            "FROM lesson l\n" +
            "JOIN course c ON l.course_id = c.course_id\n" +
            "JOIN \"user\" u ON u.email = :email \n" +
            "WHERE c.course_id = :courseId AND l.lesson_id= :lessonId \n" +
            "ON CONFLICT (lesson_id, user_id) DO UPDATE\n" +
            "SET status = 'COMPLETED';",nativeQuery = true)
    int completeLesson(UUID courseId, UUID lessonId, String email);

    @Modifying
    @Query("UPDATE UserLessonProgress ulp SET ulp.status = 'NOT_STARTED' WHERE ulp.lesson.lessonId = :lessonId AND ulp.user.email = :email")
    void unCompleteLesson(UUID lessonId, String email);

    Optional<UserLessonProgress> findByLesson_LessonIdAndUser_Email(UUID lessonId, String email);

    List<UserLessonProgress> findByUser_EmailAndLesson_Course_CourseId(String email, UUID courseId);
}
