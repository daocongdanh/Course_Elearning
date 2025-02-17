package group.project.cursusonlinecoursemanagement.course.domain.dto.payload.response;

import group.project.cursusonlinecoursemanagement.course.domain.entity.Feedback;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for {@link Feedback}
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FeedbackResponse {
    Long id;
    String feedback;
    int rating;
    UUID userUserId;
    String fullName;
    LocalDateTime createdAt;

    public static FeedbackResponse convertEntityToResponse(Feedback feedback) {
        return FeedbackResponse.builder()
                .id(feedback.getId())
                .feedback(feedback.getFeedback())
                .rating(feedback.getRating())
                .userUserId(feedback.getUser().getUserId())
                .fullName(feedback.getUser().getFullName())
                .createdAt(feedback.getCreatedAt())
                .build();
    }
}