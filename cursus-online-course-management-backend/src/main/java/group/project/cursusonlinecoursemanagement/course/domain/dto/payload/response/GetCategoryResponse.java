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
public class GetCategoryResponse {
    private static ModelMapper modelMapper = new ModelMapper();

    long categoryId;
    String categoryName;
    String categoryIcon;

    public static GetCategoryResponse convertEntityToResponse(Category category) {
        return modelMapper.map(category, GetCategoryResponse.class);
    }
}
