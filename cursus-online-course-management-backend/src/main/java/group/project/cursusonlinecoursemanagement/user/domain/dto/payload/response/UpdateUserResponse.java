package group.project.cursusonlinecoursemanagement.user.domain.dto.payload.response;

import group.project.cursusonlinecoursemanagement.user.domain.entity.Instructor;
import group.project.cursusonlinecoursemanagement.user.domain.entity.Role;
import group.project.cursusonlinecoursemanagement.user.domain.entity.Status;
import group.project.cursusonlinecoursemanagement.user.domain.entity.User;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.modelmapper.ModelMapper;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateUserResponse implements Serializable {
    private static ModelMapper modelMapper = new ModelMapper();

    private UUID userId;
    private String email;
    private String fullName;
    private String phone;
    private Status status;
    private boolean verify;
    private Set<Role> roles;
    private String avatarImageUrl;
    private Instructor instructor;
    private LocalDateTime joinDate;

    public static UpdateUserResponse convertEntityToResponse(User user) {
        return modelMapper.map(user, UpdateUserResponse.class);
    }

    public static UpdateUserResponse convertEntityToResponseBasis(User user) {
        UpdateUserResponse updateUserResponse = new UpdateUserResponse();

        updateUserResponse.setUserId(user.getUserId());
        updateUserResponse.setEmail(user.getEmail());
        updateUserResponse.setFullName(user.getFullName());
        updateUserResponse.setPhone(user.getPhone());
        updateUserResponse.setStatus(user.getStatus());
        updateUserResponse.setVerify(user.isVerify());
        updateUserResponse.setAvatarImageUrl(user.getAvatarImageKey());
        updateUserResponse.setInstructor(user.getInstructor());
        updateUserResponse.setJoinDate(user.getJoinDate());
        updateUserResponse.setRoles(user.getRoles());
        updateUserResponse.setAvatarImageUrl(user.getAvatarImageKey());
        return updateUserResponse;
    }
}
