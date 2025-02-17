package group.project.cursusonlinecoursemanagement.bmi.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "bmi")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BMI {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bmi_id", nullable = false)
    private Long bmiId;

    @Column(name = "min_bmi", nullable = false)
    private Double minBmi;

    @Column(name = "max_bmi", nullable = false)
    private Double maxBmi;

    @Column(name = "category")
    private String category;

    @Column(name = "advice", columnDefinition = "TEXT")
    private String advice;
}
