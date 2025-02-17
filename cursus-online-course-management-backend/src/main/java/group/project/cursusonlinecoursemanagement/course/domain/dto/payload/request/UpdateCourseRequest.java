package group.project.cursusonlinecoursemanagement.course.domain.dto.payload.request;

import group.project.cursusonlinecoursemanagement.course.domain.entity.Course;
import group.project.cursusonlinecoursemanagement.shared.validation.title.ValidTitle;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.util.List;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateCourseRequest {

    private static ModelMapper modelMapper = new ModelMapper();

//    @ValidTitle
    private String courseTitle;

    private String description;

    @DecimalMin(value = "0.0", message = "Price must be greater than or equal to 0.0")
    @Digits(integer = 10, fraction = 2, message = "Price must be a decimal number with up to 10 digits and 2 decimal places")
    private BigDecimal price;

    @DecimalMin(value = "0.0", message = "Discount must be greater than or equal to 0.0")
    @DecimalMax(value = "1.0", message = "Discount must be less than or equal to 1.0")
    @Digits(integer = 1, fraction = 1, message = "Discount must be a decimal number with up to 1 digits and 1 decimal places")
    private float discount;

    private boolean courseStatus;

    private List<Long> categoriesId;

    public static Course convertRequestToEntity(UpdateCourseRequest course) {
        return modelMapper.map(course, Course.class);
    }
}
