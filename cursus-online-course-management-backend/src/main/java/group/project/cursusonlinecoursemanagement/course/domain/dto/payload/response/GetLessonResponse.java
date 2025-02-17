package group.project.cursusonlinecoursemanagement.course.domain.dto.payload.response;

import group.project.cursusonlinecoursemanagement.course.domain.entity.Lesson;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.UUID;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GetLessonResponse {

    UUID lessonId;
    String lessonTitle;
    String lessonDescription;
    Integer position;
    Boolean isFree;
    Boolean isPublished;
    String videoUrl;
    String keyVideo;
    UUID courseId;
//    List<UserLessonProgressResponse> lessonProgressList;

    public static GetLessonResponse convertEntityToResponse(Lesson lesson) {
        return GetLessonResponse.builder()
                .lessonId(lesson.getLessonId())
                .lessonTitle(lesson.getLessonTitle())
                .lessonDescription(lesson.getLessonDescription())
                .position(lesson.getPosition())
                .isFree(lesson.getIsFree())
                .isPublished(lesson.getIsPublished())
                .videoUrl(lesson.getVideoUrl())
                .keyVideo(lesson.getKeyVideo())
                .courseId(lesson.getCourse().getCourseId())
//                .lessonProgressList(lesson.getLessonProgressList().stream().map(UserLessonProgressResponse::convertEntityToResponse).toList())
                .build();
    }
}
