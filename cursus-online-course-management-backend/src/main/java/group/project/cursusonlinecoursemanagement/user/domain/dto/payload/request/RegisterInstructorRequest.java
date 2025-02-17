package group.project.cursusonlinecoursemanagement.user.domain.dto.payload.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegisterInstructorRequest {
    @Schema(description = "Instructor's introduction")
    @Size(min = 2, max = 50)
    @NotEmpty
    String introduction;
    String professionalExperience;
}
