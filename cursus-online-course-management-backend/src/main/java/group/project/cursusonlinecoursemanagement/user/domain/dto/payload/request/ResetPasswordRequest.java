package group.project.cursusonlinecoursemanagement.user.domain.dto.payload.request;

import com.google.firebase.database.annotations.NotNull;
import group.project.cursusonlinecoursemanagement.shared.validation.password.ValidPassword;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ResetPasswordRequest {

    @ValidPassword
    String password;
    
    String verificationToken;
}
