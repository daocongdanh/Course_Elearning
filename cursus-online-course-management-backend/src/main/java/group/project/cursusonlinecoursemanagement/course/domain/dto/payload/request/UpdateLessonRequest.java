package group.project.cursusonlinecoursemanagement.course.domain.dto.payload.request;

import group.project.cursusonlinecoursemanagement.course.domain.entity.Lesson;
import group.project.cursusonlinecoursemanagement.shared.validation.title.ValidTitle;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.modelmapper.ModelMapper;

@Data
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateLessonRequest {
    private static ModelMapper modelMapper = new ModelMapper();

//    @ValidTitle
    private String lessonTitle;

    private String lessonDescription;
    private Boolean isFree;
    private Boolean isPublished;

    public static Lesson convertRequestToEntity(UpdateLessonRequest lesson) {
        return modelMapper.map(lesson, Lesson.class);
    }
}
