package group.project.cursusonlinecoursemanagement.user.repository;

import group.project.cursusonlinecoursemanagement.user.domain.entity.Status;
import group.project.cursusonlinecoursemanagement.user.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.*;
import java.time.LocalDateTime;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmailIgnoreCase(String email);

    Boolean existsByEmailIgnoreCase(String email);

    boolean existsByPhone(String phone);

    //Admin repository
    @Query("SELECT SUM(e.enrollmentPrice) FROM Enrollment e")
    Double sumALLEnrollmentPrice();

    @Query("SELECT SUM(e.enrollmentPrice) " +
            "FROM Enrollment e " +
//            "JOIN Course c on c.courseId = e.course.courseId " +
            "WHERE " +
            "MONTH(e.enrollmentDate) = :month " +
            "AND YEAR (e.enrollmentDate) = :year")
    Double sumEnrollmentPriceByDate(int month, int year);


    @Query("SELECT DATE(e.enrollmentDate) as date, SUM(e.enrollmentPrice * 0.1) as sum " +
            "FROM Enrollment e " +
            "WHERE e.enrollmentDate BETWEEN :startDate AND :endDate " +
            "GROUP BY DATE(e.enrollmentDate)")
    List<Map<String, Object>> sumEnrollmentPriceByDateRange(LocalDateTime startDate,LocalDateTime endDate);


    @Query("SELECT COUNT(*) FROM Enrollment e")
    int countNumberOfEnrollment();

    @Query("SELECT COUNT(*) " +
            "FROM Enrollment e " +
//            "JOIN Course c on c.courseId = e.course.courseId " +
            "WHERE " +
            "MONTH(e.enrollmentDate) = :month " +
            "AND YEAR (e.enrollmentDate) = :year")
    int countNumberOfEnrollmentByDate(int month, int year);

    @Query("SELECT e.enrollmentDate FROM Enrollment e ORDER BY e.enrollmentDate ASC LIMIT 1")
    LocalDateTime createDateOfFirstEnrollment();

    @Query("SELECT COUNT(*) FROM Course c")
    int countAllCourse();

    @Query("SELECT COUNT(*) " +
            "FROM Course u " +
            "WHERE " +
            "MONTH(u.createdDate) = :month " +
            "AND YEAR (u.createdDate) = :year")
    int countNumberOfCourseByMonth(int month, int year);

    @Query(value = "SELECT COUNT(*) FROM public.user_role WHERE role = 'ROLE_INSTRUCTOR'", nativeQuery = true)
    int countAllInstructor();

    @Query("SELECT COUNT(*) FROM User u JOIN u.roles r " +
            "WHERE r = 'ROLE_STUDENT' " +
            "AND MONTH(u.joinDate) = :month " +
            "AND YEAR (u.joinDate) = :year")
    int countStudentsByMonth(int month,int year);

    @Query("SELECT COUNT(*) FROM User u JOIN u.roles r " +
            "WHERE r = 'ROLE_INSTRUCTOR' " +
            "AND MONTH(u.joinDate) = :month " +
            "AND YEAR (u.joinDate) = :year")
    int countInstructorsByMonth(int month, int year);

    @Query("SELECT COUNT(*) FROM User c")
    int countAllUser();

    @Query("SELECT COUNT(*) " +
            "FROM User u " +
            "WHERE " +
            "MONTH(u.joinDate) = :month " +
            "AND YEAR (u.joinDate) = :year")
    int countAllUserByMonth(int month, int year);

    @Query("SELECT u.joinDate FROM User u ORDER BY u.joinDate ASC LIMIT 1")
    LocalDateTime createDateOfFirstUserRegister();

    @Query("SELECT u.status FROM User u WHERE u.email = :email")
    Status findStatusByEmail(String email);



}