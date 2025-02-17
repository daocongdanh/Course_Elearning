package group.project.cursusonlinecoursemanagement.course.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Entity
@Table(
        name = "lesson"
)
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Lesson {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "lesson_id", nullable = false)
    private UUID lessonId;

    @Column(name = "lesson_title", nullable = false)
    private String lessonTitle;
    
    @Column(name = "lesson_description", columnDefinition = "TEXT")
    private String lessonDescription;
    
    @Column(name = "position", nullable = false)
    private Integer position;
    
    @Column(name = "is_free", nullable = false)
    private Boolean isFree = false;
    
    @Column(name = "is_published", nullable = false)
    private Boolean isPublished = false;

    @Column(name = "video_url")
    private String videoUrl;

    @Column(name = "key_video")
    private String keyVideo;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "course_id")
    @ToString.Exclude
    private Course course;

    @OneToMany(mappedBy = "lesson", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<UserLessonProgress> lessonProgressList;
}
