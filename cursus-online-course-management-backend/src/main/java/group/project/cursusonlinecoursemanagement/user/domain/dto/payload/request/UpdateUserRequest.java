package group.project.cursusonlinecoursemanagement.user.domain.dto.payload.request;

import group.project.cursusonlinecoursemanagement.user.domain.entity.User;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.modelmapper.ModelMapper;

import java.io.Serializable;
import java.util.Set;
import java.util.UUID;


@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateUserRequest implements Serializable {
    private static ModelMapper modelMapper = new ModelMapper();

    UUID userId;
    String email;
    String password;
    String fullName;
    String phone;
    String avatarImageUrl;
    Set<String> roles;

    public static UpdateUserRequest convertEntityToDto(User user) {
        return modelMapper.map(user, UpdateUserRequest.class);
    }

    public static User convertDtoToEntity(UpdateUserRequest userDto) {
        return modelMapper.map(userDto, User.class);
    }
}