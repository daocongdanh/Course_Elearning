package group.project.cursusonlinecoursemanagement.user.domain.dto.payload.response;

import group.project.cursusonlinecoursemanagement.user.domain.entity.Role;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class IntrospectResponse {
    String username;
    List<String> roles;
}
