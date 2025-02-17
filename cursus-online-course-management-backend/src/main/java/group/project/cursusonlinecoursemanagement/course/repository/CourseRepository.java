package group.project.cursusonlinecoursemanagement.course.repository;

import group.project.cursusonlinecoursemanagement.course.domain.entity.Course;
import group.project.cursusonlinecoursemanagement.user.domain.dto.payload.response.CourseEnrollmentCountByDate;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CourseRepository extends JpaRepository<Course, UUID> {
    List<Course> findByUser_EmailOrderByCreatedDateDesc(String email);

    @EntityGraph(attributePaths = {"lessons"})
    Optional<Course> findById(UUID courseId);
    
    List<Course> findByCourseTitleContainsIgnoreCaseAndCategories_CategoryId(String courseTitle, Long categoryId);
    
    List<Course> findByCategories_CategoryId(Long categoryId);
    
    List<Course> findByCourseTitleContainsIgnoreCase(String title);

    List<Course> findByCourseStatus(boolean courseStatus);


    @Query("SELECT new group.project.cursusonlinecoursemanagement.user.domain.dto.payload.response.CourseEnrollmentCountByDate(c.createdDate, COUNT(c), 0) " +
            "FROM Course c WHERE c.createdDate >= :startDate GROUP BY c.createdDate")
    List<CourseEnrollmentCountByDate> findCourseCountByDate(@Param("startDate") LocalDateTime startDate);
}