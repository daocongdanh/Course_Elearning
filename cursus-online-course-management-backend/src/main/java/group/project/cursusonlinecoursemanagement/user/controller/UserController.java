package group.project.cursusonlinecoursemanagement.user.controller;

import group.project.cursusonlinecoursemanagement.user.domain.dto.UserDto;
import group.project.cursusonlinecoursemanagement.user.domain.dto.payload.request.ChangePasswordRequest;
import group.project.cursusonlinecoursemanagement.user.domain.dto.payload.request.RegisterInstructorRequest;
import group.project.cursusonlinecoursemanagement.user.domain.dto.payload.response.FullUserDetails;

import group.project.cursusonlinecoursemanagement.user.domain.dto.payload.response.GetCourseInstructorInfo;
import group.project.cursusonlinecoursemanagement.user.domain.dto.payload.response.UpdateUserResponse;
import group.project.cursusonlinecoursemanagement.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Tag(
        name = "CRUD REST APIs for User"
)
@Controller
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Endpoint to get all users.
     *
     * @return A ResponseEntity containing a list of UserDto objects for all users.
     */
    @Operation(
            summary = "Get All Users REST API",
            description = "Get All Users REST API is used to fetch all the users from the database"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Http Status 200 SUCCESS"
    )
    @SecurityRequirement(
            name = "Bear Authentication"
    )
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/auth/users")
    public ResponseEntity<?> getAllUser() {
        List<UserDto> user = userService.getAllUser();
        return ResponseEntity.ok(user);
    }

    /**
     * Endpoint to get a user by their ID.
     *
     * @param userID The UUID of the user to retrieve.
     * @return A ResponseEntity containing the UserDto of the retrieved user.
     */
    @Operation(
            summary = "Get User By Id REST API",
            description = "Get User By Id REST API is used to get single user from the database"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Http Status 200 SUCCESS"
    )
    @SecurityRequirement(
            name = "Bear Authentication"
    )
    @PreAuthorize("hasRole('ROLE_STUDENT') or hasRole('ROLE_INSTRUCTOR') or hasRole('ROLE_ADMIN')")
    @GetMapping("/auth/getUserById/{userID}")
    public ResponseEntity<?> getUserByID(@PathVariable UUID userID) {
        UserDto user = userService.getUserDtoById(userID);
        return ResponseEntity.ok(user);
    }

    /**
     * Endpoint to update a user by their ID.
     *
     * @param user   The UserDto containing the updated user details.
     * @param userID The UUID of the user to update.
     * @return A ResponseEntity containing a success message.
     */
    @Operation(
            summary = "Update User REST API",
            description = "Update User REST API is used to update a particular user from the database"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Http Status 200 SUCCESS"
    )
    @SecurityRequirement(
            name = "Bear Authentication"
    )
    @PreAuthorize("hasRole('ROLE_STUDENT') or hasRole('ROLE_INSTRUCTOR') or hasRole('ROLE_ADMIN')")
    @PutMapping("/auth/updateUser/{userID}")
    public ResponseEntity<?> updateUserById(@RequestBody UserDto user, @PathVariable UUID userID) {
        userService.updateUserById(user, userID);
        return ResponseEntity.ok("User update successfully");
    }

    @Operation(
            summary = "Upload User avatar REST API",
            description = "Upload User avatar REST API is used to upload a particular user avatar"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Http Status 200 SUCCESS"
    )
    @SecurityRequirement(
            name = "Bear Authentication"
    )
    @PreAuthorize("hasRole('ROLE_STUDENT') or hasRole('ROLE_INSTRUCTOR') or hasRole('ROLE_ADMIN')")
    @PutMapping(value = "/auth/uploadAvatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadAvatar(@RequestParam(value = "file") MultipartFile file) {
        return new ResponseEntity<>(userService.uploadAvatar(file), HttpStatus.OK);
    }

    /**
     * Endpoint to delete a user by their ID.
     *
     * @param userID The UUID of the user to delete.
     * @return A ResponseEntity containing a success message.
     */
    @Operation(
            summary = "Delete User REST API",
            description = "Delete User REST API is used to delete a particular user from the database"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Http Status 200 SUCCESS"
    )
    @SecurityRequirement(
            name = "Bear Authentication"
    )
    @PreAuthorize("hasRole('ROLE_STUDENT') or hasRole('ROLE_INSTRUCTOR') or hasRole('ROLE_ADMIN')")
    @DeleteMapping("/auth/deleteUser/{userID}")
    public ResponseEntity<?> deleteUserByID(@PathVariable UUID userID) {
        userService.deleteUser(userID);
        return ResponseEntity.ok("User delete successfully");
    }

    /**
     * Endpoint to get a user.
     *
     * @return A ResponseEntity containing the UserDto of the retrieved user.
     */
    @Operation(
            summary = "Get User REST API",
            description = "Get User REST API is used to get single user from the database"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Http Status 200 SUCCESS"
    )
    @SecurityRequirement(
            name = "Bear Authentication"
    )
    @PreAuthorize("hasRole('ROLE_STUDENT') or hasRole('ROLE_INSTRUCTOR') or hasRole('ROLE_ADMIN')")
    @GetMapping("/auth/getUser")
    public ResponseEntity<?> getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return ResponseEntity.ok(userService.getUserDtoByEmail(email));
    }

    @Operation(
            summary = "Get User By Id REST API",
            description = "Get User By Id REST API is used to get a single user from the database",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User fetched successfully"),
                    @ApiResponse(responseCode = "400", description = "Bad Request"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    @SecurityRequirement(
            name = "Bear Authentication"
    )
    @GetMapping("/auth/getUserById")
    public ResponseEntity<?> getUser(@RequestParam UUID userId) {
        return ResponseEntity.ok(userService.getUserDtoHavaImageById(userId));
    }

    @Operation(
            summary = "Get Instructor By Id REST API",
            description = "Get Instructor By Id REST API is used to get a single instructor from the database",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Instructor fetched successfully"),
                    @ApiResponse(responseCode = "400", description = "Bad Request"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    @SecurityRequirement(
            name = "Bear Authentication"
    )
    @GetMapping("/auth/getInstructorHavaImageById")
    public ResponseEntity<?> getInstructor(@RequestParam UUID userId) {
        return ResponseEntity.ok(userService.getInstructorDtoHavaImageById(userId));
    }

    /**
     * Endpoint to get full user details of the currently logged-in user.
     *
     * @return A ResponseEntity containing the FullUserDetails of the authenticated user.
     */
    @Operation(
            summary = "Get Full User Details Of The Currently Logged In User REST API",
            description = "Get Full User Details REST API is used to fetch the full details of the authenticated user",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Full user details fetched successfully"),
                    @ApiResponse(responseCode = "400", description = "Bad Request"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    @SecurityRequirement(
            name = "Bear Authentication"
    )
    @GetMapping("/auth/getFullUserDetails")
    public ResponseEntity<FullUserDetails> getFullUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return ResponseEntity.ok(userService.getFullUserDetailsByEmail(email));
    }

    /**
     * Endpoint to update a user.
     *
     * @param user The UpdateUserResponse containing the updated user details.
     * @return A ResponseEntity containing a success message.
     */
    @Operation(
            summary = "Update User REST API",
            description = "Update User REST API is used to update a particular user from the database",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User update successfully"),
                    @ApiResponse(responseCode = "400", description = "Bad Request"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    @SecurityRequirement(
            name = "Bear Authentication"
    )
    @SecurityRequirement(
            name = "Bear Authentication"
    )
    @PutMapping("/auth/updateUser")
    public ResponseEntity<?> updateUser(@RequestBody UpdateUserResponse user) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        userService.updateUserByEmail(user, email);
        return ResponseEntity.ok("User update successfully");
    }

    /**
     * Endpoint to change the password of a user.
     *
     * @param changePasswordRequest The request containing the current and new password details.
     * @return A ResponseEntity containing a success message.
     */
    @Operation(
            summary = "Change Password REST API",
            description = "Change Password REST API is used to change the password of a user",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Password update successfully"),
                    @ApiResponse(responseCode = "400", description = "Bad Request"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    @SecurityRequirement(
            name = "Bear Authentication"
    )
    @PatchMapping("/auth/changePassword")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest changePasswordRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        userService.updatePassword(email, changePasswordRequest);
        return ResponseEntity.ok("Password update successfully");
    }

    /**
     * Endpoint to register a user as an instructor.
     *
     * @param registerInstructorRequest The request containing the details required to register as an instructor.
     * @return A ResponseEntity containing a success message.
     */
    @Operation(
            summary = "Register Become Instructor REST API",
            description = "Register Become Instructor REST API is used to register a user as an instructor",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Register become instructor successfully"),
                    @ApiResponse(responseCode = "400", description = "Bad Request"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    @SecurityRequirement(
            name = "Bear Authentication"
    )
    @PutMapping("/auth/registerBecomeInstructor")
    public ResponseEntity<?> registerBecomeInstructor(@RequestBody RegisterInstructorRequest registerInstructorRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        userService.registerBecomeInstructor(email, registerInstructorRequest);
        return ResponseEntity.ok("Register become instructor successfully");
    }

    @GetMapping("/getInstructorById/{instructorId}")
    public ResponseEntity<GetCourseInstructorInfo> getInstructorById(@PathVariable UUID instructorId){
        return ResponseEntity.ok(userService.getInstructorById(instructorId));
    }
}
