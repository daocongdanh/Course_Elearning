package group.project.cursusonlinecoursemanagement.course.domain.dto.payload.response;

import group.project.cursusonlinecoursemanagement.course.domain.entity.Lesson;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LessonResponse {
    
    private UUID lessonId;
    private String lessonTitle;
    private String lessonDescription;
    private Integer position;
    private Boolean isFree;
    private Boolean isPublished;
    private String videoUrl;
    private List<UserLessonProgressResponse> lessonProgressList;

    public static LessonResponse convertEntityToResponse(Lesson lesson) {
        LessonResponse lessonResponse = new LessonResponse();
        lessonResponse.setLessonId(lesson.getLessonId());
        lessonResponse.setLessonTitle(lesson.getLessonTitle());
        lessonResponse.setLessonDescription(lesson.getLessonDescription());
        lessonResponse.setPosition(lesson.getPosition());
        lessonResponse.setIsFree(lesson.getIsFree());
        lessonResponse.setIsPublished(lesson.getIsPublished());
        lessonResponse.setVideoUrl(lesson.getVideoUrl());
        lessonResponse.setLessonProgressList(lesson.getLessonProgressList().stream().map(UserLessonProgressResponse::convertEntityToResponse).toList());
        return lessonResponse;
    }
}
