package group.project.cursusonlinecoursemanagement.user.domain.entity;

import group.project.cursusonlinecoursemanagement.course.domain.entity.Course;
import group.project.cursusonlinecoursemanagement.course.domain.entity.Enrollment;
import group.project.cursusonlinecoursemanagement.course.domain.entity.UserLessonProgress;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(
        name = "\"user\"",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "email"),
                @UniqueConstraint(columnNames = "phone")
        }
)
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 200)
    private String fullName;

    @Column(length = 10, unique = true, columnDefinition = "VARCHAR(10) CHECK (phone ~ '^[0-9]+$')")
    private String phone;

    @Column(nullable = false, name = "status")
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(nullable = false)
    private boolean verify = false;

    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Set<Role> roles = new HashSet<>();

    private String avatarImageKey;

    @OneToMany(mappedBy = "user")
    @ToString.Exclude
    private Set<Enrollment> enrollments;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<UserLessonProgress> lessonProgressList;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<Course> courseList;
    
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Instructor instructor;

    @CreatedDate
    @Column(name = "join_date")
    private LocalDateTime joinDate;
}
