package group.project.cursusonlinecoursemanagement.user.domain.dto.payload.response;

import group.project.cursusonlinecoursemanagement.course.domain.dto.payload.response.EnrollmentResponse;
import group.project.cursusonlinecoursemanagement.user.domain.entity.Role;
import group.project.cursusonlinecoursemanagement.user.domain.entity.Status;
import group.project.cursusonlinecoursemanagement.user.domain.entity.User;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GetStudentResponse {
    UUID userId;
    String fullName;
    String email;
    String phone;
    Status status;
    boolean verify;
    List<Role> roles;
    List<EnrollmentResponse> enrollmentsResponse;

    public static GetStudentResponse convertUserToUserWithEnrollment(User user) {
        GetStudentResponse getStudentResponse = new GetStudentResponse();

        getStudentResponse.setUserId(user.getUserId());
        getStudentResponse.setFullName(user.getFullName());
        getStudentResponse.setEmail(user.getEmail());
        getStudentResponse.setPhone(user.getPhone());
        getStudentResponse.setStatus(user.getStatus());
        getStudentResponse.setVerify(user.isVerify());
        getStudentResponse.setRoles(user.getRoles().stream().toList());
        getStudentResponse.setEnrollmentsResponse(
                user.getEnrollments().stream()
                .map(EnrollmentResponse::convertEntityToResponse)
                .collect(Collectors.toList())
        );

        return getStudentResponse;
    }
}
