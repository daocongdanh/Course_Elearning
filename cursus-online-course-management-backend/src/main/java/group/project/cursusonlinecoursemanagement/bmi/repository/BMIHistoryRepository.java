package group.project.cursusonlinecoursemanagement.bmi.repository;

import group.project.cursusonlinecoursemanagement.bmi.domain.entity.BMIHistory;
import group.project.cursusonlinecoursemanagement.user.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BMIHistoryRepository extends JpaRepository<BMIHistory, Long> {
    Optional<BMIHistory> findTopByUserOrderByRecordedAtDesc(User user);

    @Query("""
        SELECT b.recordedAt, b.weight, b.height, b.bmi 
        FROM BMIHistory b 
        WHERE YEAR(b.recordedAt) = :year 
          AND MONTH(b.recordedAt) = :month 
          AND b.user.id = :userId
          AND b.recordedAt = (
              SELECT MAX(b2.recordedAt) 
              FROM BMIHistory b2 
              WHERE DATE(b2.recordedAt) = DATE(b.recordedAt) 
                AND b2.user.id = :userId
          )
        ORDER BY b.recordedAt ASC
    """)
    List<Object[]> findDailyBMI(@Param("year") int year, @Param("month") int month, @Param("userId") UUID userId);
}
