package group.project.cursusonlinecoursemanagement.user.service.impl;

import com.cloudinary.Cloudinary;
import group.project.cursusonlinecoursemanagement.shared.domain.FileMetadata;
import group.project.cursusonlinecoursemanagement.shared.exception.handler.ResourceNotFoundException;
import group.project.cursusonlinecoursemanagement.shared.service.CloudinaryService;
import group.project.cursusonlinecoursemanagement.user.domain.dto.InstructorDto;
import group.project.cursusonlinecoursemanagement.user.domain.dto.UserDto;
import group.project.cursusonlinecoursemanagement.user.domain.dto.payload.request.ChangePasswordRequest;
import group.project.cursusonlinecoursemanagement.user.domain.dto.payload.request.RegisterInstructorRequest;
import group.project.cursusonlinecoursemanagement.user.domain.dto.payload.response.FullUserDetails;
import group.project.cursusonlinecoursemanagement.user.domain.dto.payload.response.GetCourseInstructorInfo;
import group.project.cursusonlinecoursemanagement.user.domain.dto.payload.response.UpdateUserResponse;
import group.project.cursusonlinecoursemanagement.user.domain.entity.Instructor;
import group.project.cursusonlinecoursemanagement.user.domain.entity.Role;
import group.project.cursusonlinecoursemanagement.user.domain.entity.Status;
import group.project.cursusonlinecoursemanagement.user.domain.entity.User;
import group.project.cursusonlinecoursemanagement.user.repository.InstructorRepository;
import group.project.cursusonlinecoursemanagement.user.repository.UserRepository;
import group.project.cursusonlinecoursemanagement.user.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final InstructorRepository instructorRepository;
    private final ModelMapper modelMapper;
    private final CloudinaryService cloudinaryService;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, InstructorRepository instructorRepository, ModelMapper modelMapper, CloudinaryService cloudinaryService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.instructorRepository = instructorRepository;
        this.modelMapper = modelMapper;
        this.cloudinaryService = cloudinaryService;
    }

    /**
     * Loads a user by their username.
     *
     * @param username The username of the user to load.
     * @return A UserDetails object containing the user's details.
     * @throws UsernameNotFoundException if the user is not found.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userDetail = userRepository.findByEmailIgnoreCase(username);
        return userDetail.map(UserInfoDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("User not found " + username));
    }

    /**
     * Retrieves a UserDto object by its ID.
     *
     * @param userID The UUID of the user.
     * @return A UserDto object containing the user's details.
     */
    @Override
    public UserDto getUserDtoById(UUID userID) {
        return UserDto.convertEntityToDto(getUserFromRepoById(userID));
    }

    @Override
    public UserDto getUserDtoHavaImageById(UUID userID) {
        User user = getUserFromRepoById(userID);
        UserDto userDto = UserDto.convertEntityToDto(user);
        return userDto;
    }




    /**
     * Retrieves a User object by its ID from the repository.
     *
     * @param userId The UUID of the user.
     * @return A User object containing the user's details.
     * @throws ResourceNotFoundException if the user is not found.
     */
    private User getUserFromRepoById(UUID userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new ResourceNotFoundException("User", "userId", userId.toString())
        );
    }

    /**
     * Retrieves a list of all UserDto objects.
     *
     * @return A list of UserDto objects containing the details of all users.
     */
    @Override
    public List<UserDto> getAllUser() {
        List<User> userList = userRepository.findAll();

        return userList.stream()
                .map(UserDto::convertEntityToDto)
                .collect(Collectors.toList());
    }


    /**
     * Updates a user with the provided UserDto and user ID.
     *
     * @param userDto The UserDto containing the user's updated details.
     * @param uuid    The UUID of the user to update.
     * @throws ResourceNotFoundException if the user is not found.
     */
    @Override
    public void updateUserById(UserDto userDto, UUID uuid) {
        User user = userRepository.findById(uuid).orElseThrow(
                () -> new ResourceNotFoundException("User", "userId", uuid.toString()));
        user.setEmail(userDto.getEmail());
        user.setPassword(userDto.getPassword());
        user.setPhone(userDto.getPhone());
        user.setFullName(userDto.getFullName());
        userRepository.save(user);
    }

    @Override
    public String uploadAvatar(MultipartFile file) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // Lấy user
        User user = findUserByEmail(authentication.getName());
        String image = cloudinaryService.uploadFile(file, "image");
        user.setAvatarImageKey(image);
        userRepository.save(user);
        UpdateUserResponse response = UpdateUserResponse.convertEntityToResponseBasis(user);
        return response.getAvatarImageUrl();
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email).orElseThrow(
                () -> new ResourceNotFoundException("User", "email", email)
        );
    }

    @Override
    public GetCourseInstructorInfo getInstructorById(UUID instructorId) {
        User instructor = getUserFromRepoById(instructorId);
        GetCourseInstructorInfo instructorDto = GetCourseInstructorInfo.convertEntityToResponse(instructor);
        return instructorDto;
    }

    @Override
    public Status findStatusByEmail(String email) {
        return userRepository.findStatusByEmail(email);
    }

    @Override
    public InstructorDto getInstructorDtoHavaImageById(UUID instructorId) {
        Instructor instructor = instructorRepository.getInstructorByUserId(instructorId).orElseThrow(() ->
                new ResourceNotFoundException("Instructor", "ID", instructorId.toString()));

        InstructorDto instructorDto = InstructorDto.convertEntityToDto(instructor);
        return instructorDto;
    }


    /**
     * Deletes a user with the provided user ID.
     *
     * @param userID The UUID of the user to delete.
     * @throws ResourceNotFoundException if the user is not found.
     */
    @Override
    public void deleteUser(UUID userID) {
        userRepository.delete(getUserFromRepoById(userID));
    }

    @Override
    public UserDto getUserDtoByEmail(String email) {
        User user = getUserFromRepoByEmail(email);
        return UserDto.convertEntityToDto(user);
    }

    @Override
    public void updateUserByEmail(UpdateUserResponse userDto, String email) {
        User user = getUserFromRepoByEmail(email);
        user.setPhone(userDto.getPhone());
        user.setFullName(userDto.getFullName());
        userRepository.save(user);
    }

    @Override
    public void updatePassword(String email, ChangePasswordRequest changePasswordRequest) {
        User user = getUserFromRepoByEmail(email);
        if (!passwordEncoder.matches(changePasswordRequest.getCurrentPassword(), user.getPassword())){
            throw new IllegalArgumentException("Old password is incorrect");
        }
        user.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    public void registerBecomeInstructor(String email, RegisterInstructorRequest registerInstructorRequest) {
        User user = getUserFromRepoByEmail(email);

        if(!user.getRoles().contains(Role.ROLE_INSTRUCTOR)){
            user.getRoles().add(Role.ROLE_INSTRUCTOR);
            userRepository.save(user);

            Instructor instructor = user.getInstructor();
            if (instructor == null) {
                instructor = new Instructor();
                instructor.setUser(user);
                user.setInstructor(instructor);
            }


            instructor.setJoinDate(LocalDateTime.now());
            instructor.setIntroduction(registerInstructorRequest.getIntroduction());
            instructor.setProfessionalExperience(registerInstructorRequest.getProfessionalExperience());

            instructorRepository.save(instructor);
        } else {
            Instructor instructor = user.getInstructor();
            instructor.setIntroduction(registerInstructorRequest.getIntroduction());
            instructor.setProfessionalExperience(registerInstructorRequest.getProfessionalExperience());
            instructorRepository.save(instructor);
        }


    }

    @Override
    public FullUserDetails getFullUserDetailsByEmail(String email) {
        User user = getUserFromRepoByEmail(email);
        FullUserDetails fullUserDetails = FullUserDetails.convertEntityToDto(user);
        return fullUserDetails;
    }

    @Override
    public boolean isInstructor(String email) {
        User user = getUserFromRepoByEmail(email);
        return user.getRoles().contains(Role.ROLE_INSTRUCTOR);
    }

    public User getUserFromRepoByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email).orElseThrow(
                () -> new ResourceNotFoundException("User", "email", email)
        );
    }

}
