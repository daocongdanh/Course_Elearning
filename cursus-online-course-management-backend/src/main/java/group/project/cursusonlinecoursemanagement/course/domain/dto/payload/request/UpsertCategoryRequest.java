package group.project.cursusonlinecoursemanagement.course.domain.dto.payload.request;

import com.google.firebase.database.annotations.NotNull;
import group.project.cursusonlinecoursemanagement.course.domain.entity.Category;
import group.project.cursusonlinecoursemanagement.shared.validation.title.ValidTitle;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.modelmapper.ModelMapper;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpsertCategoryRequest {

    private static ModelMapper modelMapper = new ModelMapper();

    @ValidTitle(maxLength = 50,nullable = false)
    private String categoryName;

    @NotNull
    private String categoryIcon;

    public static Category convertRequestToEntity(UpsertCategoryRequest upsertCategoryRequest) {
        return modelMapper.map(upsertCategoryRequest, Category.class);
    }
}
