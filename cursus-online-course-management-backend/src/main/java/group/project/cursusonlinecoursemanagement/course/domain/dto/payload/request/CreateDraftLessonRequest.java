package group.project.cursusonlinecoursemanagement.course.domain.dto.payload.request;

import com.google.firebase.database.annotations.NotNull;
import group.project.cursusonlinecoursemanagement.course.domain.entity.Lesson;
import group.project.cursusonlinecoursemanagement.shared.validation.title.ValidTitle;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.modelmapper.ModelMapper;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateDraftLessonRequest {

    private static ModelMapper modelMapper = new ModelMapper();

    @ValidTitle(nullable = false)
    private String title;

    public static Lesson convertRequestToEntity(CreateDraftLessonRequest newLesson) {
        return modelMapper.map(newLesson, Lesson.class);
    }
}
