package group.project.cursusonlinecoursemanagement.course.domain.dto.payload.request;

import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * DTO for {@link group.project.cursusonlinecoursemanagement.course.domain.entity.Feedback}
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FeedbackRequest {
    String feedback;

    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be less than or equal to 5")
    int rating;
}