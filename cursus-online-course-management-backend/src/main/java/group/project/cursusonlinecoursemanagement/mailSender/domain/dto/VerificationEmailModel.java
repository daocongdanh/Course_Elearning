package group.project.cursusonlinecoursemanagement.mailSender.domain.dto;


import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VerificationEmailModel {
    String fullName;
    String username;
    String buttonLink;
}
