package group.project.cursusonlinecoursemanagement.course.domain.dto.payload.response;

import group.project.cursusonlinecoursemanagement.course.domain.entity.Category;
import group.project.cursusonlinecoursemanagement.course.domain.entity.Course;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.modelmapper.ModelMapper;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GetCategoryWithCourseResponse {
    private static ModelMapper modelMapper = new ModelMapper();

    long categoryId;

    String categoryName;

    String categoryIcon;

    List<GetCourseResponse> courses;

    public static GetCategoryWithCourseResponse convertEntityToResponse(Category category) {
        return modelMapper.map(category, GetCategoryWithCourseResponse.class);
    }
}
