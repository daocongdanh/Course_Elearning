package group.project.cursusonlinecoursemanagement.course.domain.dto.payload.request;

import lombok.*;

import java.util.UUID;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReorderLessonRequest {
    private UUID lessonId;
    private Integer position;
}
