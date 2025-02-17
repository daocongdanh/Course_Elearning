package group.project.cursusonlinecoursemanagement.course.domain.dto.payload.response;

import group.project.cursusonlinecoursemanagement.course.domain.entity.LessonStatus;
import group.project.cursusonlinecoursemanagement.course.domain.entity.UserLessonProgress;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserLessonProgressResponse {
    UUID userId;
    UUID lessonId;
    String lessonTitle;
    String lessonStatus;
    boolean isCompleted;
    float duration;

    public static UserLessonProgressResponse convertEntityToResponse(UserLessonProgress userLessonProgress) {
        return UserLessonProgressResponse.builder()
                .userId(userLessonProgress.getUser().getUserId())
                .lessonId(userLessonProgress.getLesson().getLessonId())
                .lessonTitle(userLessonProgress.getLesson().getLessonTitle())
                .lessonStatus(userLessonProgress.getStatus().name())
                .isCompleted(userLessonProgress.getStatus().equals(LessonStatus.COMPLETED))
                .duration(userLessonProgress.getDuration() == null ? 0 : userLessonProgress.getDuration())
                .build();
    }
}
