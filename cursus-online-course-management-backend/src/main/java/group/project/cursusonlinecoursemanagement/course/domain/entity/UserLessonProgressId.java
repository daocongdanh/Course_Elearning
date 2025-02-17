package group.project.cursusonlinecoursemanagement.course.domain.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class UserLessonProgressId implements Serializable {
    private UUID lessonId;
    private UUID userId;
}
