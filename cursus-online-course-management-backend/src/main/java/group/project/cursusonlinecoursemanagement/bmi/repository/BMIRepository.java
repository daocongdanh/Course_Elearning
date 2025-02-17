package group.project.cursusonlinecoursemanagement.bmi.repository;

import group.project.cursusonlinecoursemanagement.bmi.domain.entity.BMI;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BMIRepository extends JpaRepository<BMI, Long> {
    @Query("SELECT COUNT(b) FROM BMI b WHERE " +
            "(b.minBmi <= :maxBmi AND b.maxBmi >= :minBmi)")
    Long countOverlappingBMIs1(@Param("minBmi") Double minBmi, @Param("maxBmi") Double maxBmi);

    @Query("SELECT COUNT(b) FROM BMI b WHERE " +
            "(b.minBmi <= :maxBmi AND b.maxBmi >= :minBmi) " +
            "AND b.bmiId <> :id")
    Long countOverlappingBMIs2(@Param("minBmi") Double minBmi,
                              @Param("maxBmi") Double maxBmi,
                              @Param("id") Long id);

    @Query("SELECT b FROM BMI b WHERE :bmiValue BETWEEN b.minBmi AND b.maxBmi")
    Optional<BMI> findByBmiValue(@Param("bmiValue") Double bmiValue);

}
