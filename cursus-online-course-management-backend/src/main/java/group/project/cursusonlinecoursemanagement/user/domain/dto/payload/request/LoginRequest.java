package group.project.cursusonlinecoursemanagement.user.domain.dto.payload.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LoginRequest {

    @Schema(description = "User's email")
    String email;

    @Schema(description = "User's password")
    String password;
}
