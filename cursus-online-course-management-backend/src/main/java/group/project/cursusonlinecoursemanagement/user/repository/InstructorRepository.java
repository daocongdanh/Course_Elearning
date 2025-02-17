package group.project.cursusonlinecoursemanagement.user.repository;

import group.project.cursusonlinecoursemanagement.user.domain.entity.Instructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface InstructorRepository extends JpaRepository<Instructor, Long> {

    @Query("SELECT SUM(e.enrollmentPrice) FROM Enrollment e WHERE e.course.user.userId= :instructorId")
    Double sumEnrollmentPriceByInstructorId(UUID instructorId);

    @Query("SELECT SUM(e.enrollmentPrice) " +
            "FROM Enrollment e " +
//            "JOIN Course c on c.courseId = e.course.courseId " +
            "WHERE e.course.user.userId= :instructorId " +
            "AND MONTH(e.enrollmentDate) = :month " +
            "AND YEAR (e.enrollmentDate) = :year")
    Double sumEnrollmentPriceByInstructorIdAndDate(UUID instructorId,  int month, int year);

    @Query("SELECT COUNT(*) FROM Enrollment e WHERE e.course.user.userId = :instructorId")
    int countNumberOfEnrollmentOfInstructor(UUID instructorId);

    @Query("SELECT COUNT(*) " +
            "FROM Enrollment e " +
//            "JOIN Course c on c.courseId = e.course.courseId " +
            "WHERE e.course.user.userId= :instructorId " +
            "AND MONTH(e.enrollmentDate) = :month " +
            "AND YEAR (e.enrollmentDate) = :year")
    int countNumberOfEnrollmentByInstructorIdAndDate(UUID instructorId,  int month, int year);

    @Query("SELECT e.enrollmentDate FROM Enrollment e WHERE e.course.user.userId = :instructorId  ORDER BY e.enrollmentDate ASC LIMIT 1")
    LocalDateTime createDateOfFirstEnrollment(UUID instructorId);

    @Query("SELECT COUNT(*) FROM Course c WHERE c.user.userId = :instructorId")
    int countAllCourseOfInstructor(UUID instructorId);

    @Query("SELECT i.walletBalance FROM Instructor i WHERE i.user.email = :email")
    double getWalletBalance(String email);

    @Query("SELECT i FROM Instructor i WHERE i.userId = :instructorId")
    Optional<Instructor> getInstructorByUserId(UUID instructorId);

    @Query("SELECT SUM(e.enrollmentPrice)" +
            "FROM Enrollment e " +
            "WHERE e.course.user.email = :email " +
            "AND e.enrollmentDate >= :startDate")
    Double quarterlyRevenue(LocalDateTime startDate, String email);

    @Query("SELECT COUNT(*) " +
            "FROM Enrollment e " +
            "WHERE e.course.user.email = :email " +
            "AND e.enrollmentDate >= :startDate")
    int countNumberOfEnrollmentByQuarterOfInstructor(LocalDateTime startDate, String email);

    @Query("SELECT SUM(e.enrollmentPrice) " +
            "FROM Enrollment e " +
            "WHERE e.course.user.email = :email " +
            "AND e.course.courseId = :courseId " +
            "AND e.enrollmentDate >= :startDate ")
    Double quarterlyRevenueOfEachCourse(LocalDateTime startDate, String email, UUID courseId);

    @Query("select i from Instructor i where i.user.userId = :userId")
    Optional<Instructor> findByUser_UserId(UUID userId);

    Optional<Instructor> findByUser_Email(String email);

}