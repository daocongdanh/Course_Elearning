package group.project.cursusonlinecoursemanagement.user.domain.dto.payload.request;

import com.google.firebase.database.annotations.NotNull;
import group.project.cursusonlinecoursemanagement.shared.validation.password.ValidPassword;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegisterRequest{

    @Schema(description = "User's name")
    @Size(min = 2, max = 50)
    @NotEmpty
    String fullName;

    @ValidPassword
    String password;

    @Schema(description = "User's email")
    @NotBlank
    @Email
    String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "\\d{10}", message = "Phone number must have 10 digits")
    String phone;
}
