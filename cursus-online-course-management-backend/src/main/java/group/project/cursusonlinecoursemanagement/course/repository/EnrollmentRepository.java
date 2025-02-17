package group.project.cursusonlinecoursemanagement.course.repository;

import group.project.cursusonlinecoursemanagement.course.domain.entity.Enrollment;
import group.project.cursusonlinecoursemanagement.user.domain.dto.payload.response.CourseEnrollmentCountByDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long>, JpaSpecificationExecutor<Enrollment> {

    @Query("SELECT e FROM Enrollment e where e.user.userId= :userID")
    List<Enrollment> findEnrollmentByUserId(@Param("userID") Long userID);

    Optional<Enrollment> findByCourse_CourseIdAndUser_Email(UUID courseId, String email);

    boolean existsByCourse_CourseIdAndUser_Email(UUID courseId, String email);

    List<Enrollment> findByUser_Email(String email);

    @Query("SELECT new group.project.cursusonlinecoursemanagement.user.domain.dto.payload.response.CourseEnrollmentCountByDate(e.enrollmentDate, 0, COUNT(e)) " +
            "FROM Enrollment e WHERE e.enrollmentDate >= :startDate GROUP BY e.enrollmentDate")
    List<CourseEnrollmentCountByDate> findEnrollmentCountByDate(@Param("startDate") LocalDateTime startDate);
}
