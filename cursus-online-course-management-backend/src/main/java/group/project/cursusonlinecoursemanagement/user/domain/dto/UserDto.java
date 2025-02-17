package group.project.cursusonlinecoursemanagement.user.domain.dto;

import group.project.cursusonlinecoursemanagement.user.domain.entity.Status;
import group.project.cursusonlinecoursemanagement.user.domain.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

/**
 * DTO for {@link User}
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto implements Serializable {
    private static ModelMapper modelMapper = new ModelMapper();

    UUID userId;
    String email;
    String password;
    String fullName;
    String phone;
    Status status;
    String avatarImageUrl;
    Set<String> roles;
    LocalDateTime joinDate;

    public static UserDto convertEntityToDto(User user) {
        return modelMapper.map(user, UserDto.class);
    }

    public static User convertDtoToEntity(UserDto userDto) {
        return modelMapper.map(userDto, User.class);
    }
}