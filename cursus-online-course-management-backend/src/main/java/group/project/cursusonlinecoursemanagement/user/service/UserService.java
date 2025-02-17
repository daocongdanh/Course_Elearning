package group.project.cursusonlinecoursemanagement.user.service;

import group.project.cursusonlinecoursemanagement.user.domain.dto.InstructorDto;
import group.project.cursusonlinecoursemanagement.user.domain.dto.UserDto;
import group.project.cursusonlinecoursemanagement.user.domain.dto.payload.request.ChangePasswordRequest;
import group.project.cursusonlinecoursemanagement.user.domain.dto.payload.request.RegisterInstructorRequest;
import group.project.cursusonlinecoursemanagement.user.domain.dto.payload.response.FullUserDetails;
import group.project.cursusonlinecoursemanagement.user.domain.dto.payload.response.GetCourseInstructorInfo;
import group.project.cursusonlinecoursemanagement.user.domain.dto.payload.response.UpdateUserResponse;
import group.project.cursusonlinecoursemanagement.user.domain.entity.Status;
import group.project.cursusonlinecoursemanagement.user.domain.entity.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

/**
 * UserService is an interface that extends UserDetailsService and provides the contract for user management services.
 * It provides methods for getting a user by ID, getting all users, updating a user, and deleting a user.
 */
public interface UserService extends UserDetailsService {
    
    UserDto getUserDtoById(UUID userID);

    UserDto getUserDtoHavaImageById(UUID userID);

    List<UserDto> getAllUser();

    void updateUserById(UserDto user, UUID userId);

    void deleteUser(UUID userID);

    UserDto getUserDtoByEmail(String email);
    
    void updateUserByEmail(UpdateUserResponse userDto, String email);
    
    void updatePassword(String email, ChangePasswordRequest changePasswordRequest);
    
    void registerBecomeInstructor(String email, RegisterInstructorRequest registerInstructorRequest);
    
    FullUserDetails getFullUserDetailsByEmail(String email);
    
    boolean isInstructor(String email);
    
    User getUserFromRepoByEmail(String username);

    String uploadAvatar(MultipartFile file);

    User findUserByEmail(String email);

    GetCourseInstructorInfo getInstructorById(UUID instructorId);

    Status findStatusByEmail(String email);

    InstructorDto getInstructorDtoHavaImageById(UUID instructorId);
}