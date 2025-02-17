package group.project.cursusonlinecoursemanagement.user.domain.dto.payload.response;

import group.project.cursusonlinecoursemanagement.user.domain.entity.Status;
import group.project.cursusonlinecoursemanagement.user.domain.entity.User;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FullUserDetails {
    UUID userId;
    String fullName;
    String email;
    String phone;
    String avatarImageUrl;
    Status status;
    boolean verify;
    Set<String> roles;
    String introduction;
    String professionalExperience;

    public static FullUserDetails convertEntityToDto(User user) {
        return FullUserDetails.builder()
                .avatarImageUrl(user.getAvatarImageKey())
                .userId(user.getUserId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .status(user.getStatus())
                .verify(user.isVerify())
                .roles(user.getRoles().stream().map(Enum::name).collect(Collectors.toSet()))
                .introduction(user.getInstructor() != null ? user.getInstructor().getIntroduction() : null)
                .professionalExperience(user.getInstructor() != null ? user.getInstructor().getProfessionalExperience() : null)
                .build();
    }
}
