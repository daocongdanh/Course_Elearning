package group.project.cursusonlinecoursemanagement.course.domain.dto.payload.request;

import com.google.firebase.database.annotations.NotNull;
import group.project.cursusonlinecoursemanagement.course.domain.entity.Course;
import group.project.cursusonlinecoursemanagement.shared.validation.title.ValidTitle;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.modelmapper.ModelMapper;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateDraftCourseRequest {

    private static ModelMapper modelMapper = new ModelMapper();

    @ValidTitle(nullable = false)
    private String courseTitle;

    public static Course convertRequestToEntity(CreateDraftCourseRequest newCourse) {
        return modelMapper.map(newCourse, Course.class);
    }
}