package group.project.cursusonlinecoursemanagement.course.domain.dto.payload.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateDraftCourseResponse {
    UUID courseId;
    String courseTitle;
}
