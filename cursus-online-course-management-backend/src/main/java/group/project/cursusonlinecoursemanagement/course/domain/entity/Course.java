package group.project.cursusonlinecoursemanagement.course.domain.entity;

import group.project.cursusonlinecoursemanagement.user.domain.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(
        name = "course"
)
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "course_id", nullable = false)
    private UUID courseId;
    
    @Column(name = "course_title", nullable = false)
    private String courseTitle;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdDate;
    
    @LastModifiedDate
    @Column(updatable = false)
    private LocalDateTime modifiedDate;
    
    @Column(name = "course_status")
    private boolean courseStatus = false;
    
    @OneToMany(mappedBy = "course")
    @ToString.Exclude
    private List<Enrollment> enrollments;
    
    @Column(name = "key_thumbnail")
    private String keyThumbnail;
    
    private BigDecimal price;
    
    private float discount;
    
    @OneToMany(mappedBy = "course", targetEntity = Lesson.class, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @MapKey(name = "lessonId")
    @ToString.Exclude
    private List<Lesson> lessons;
    
    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(
            name = "course_category",
            joinColumns = @JoinColumn(name = "course_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    @ToString.Exclude
    private List<Category> categories;
    
    @CreatedBy
    @ManyToOne(fetch = FetchType.LAZY, targetEntity = User.class)
    @JoinColumn(name = "user_id")
    @ToString.Exclude
    private User user;

    @PreRemove
    private void preRemove() {
        for (Category category : categories) {
            category.getCourses().remove(this);
        }
    }

    @PreUpdate
    private void preUpdate() {
        for (Category category : categories) {
            category.getCourses().add(this);
        }
    }
}
