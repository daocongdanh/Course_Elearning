package group.project.cursusonlinecoursemanagement.course.domain.entity;

import group.project.cursusonlinecoursemanagement.user.domain.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "enrollment"
)
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Enrollment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "enrollment_id", nullable = false)
    private Long enrollmentId;

    @CreatedDate
    @Column(name = "enrollment_date")
    private LocalDateTime enrollmentDate;

    @Column(name = "enrollment_price", nullable = false)
    private Double enrollmentPrice;

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = User.class)
    @JoinColumn(name = "user_id")
    @ToString.Exclude
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = Course.class)
    @JoinColumn(name = "course_id")
    @ToString.Exclude
    private Course course;
}
