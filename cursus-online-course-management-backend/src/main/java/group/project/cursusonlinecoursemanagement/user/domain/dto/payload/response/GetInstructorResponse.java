package group.project.cursusonlinecoursemanagement.user.domain.dto.payload.response;

import group.project.cursusonlinecoursemanagement.course.domain.dto.payload.response.EnrollmentResponse;
import group.project.cursusonlinecoursemanagement.course.domain.dto.payload.response.GetCategoryWithCourseResponse;
import group.project.cursusonlinecoursemanagement.course.domain.dto.payload.response.GetCourseResponse;
import group.project.cursusonlinecoursemanagement.course.domain.dto.payload.response.LessonResponse;
import group.project.cursusonlinecoursemanagement.course.domain.entity.Category;
import group.project.cursusonlinecoursemanagement.user.domain.entity.Role;
import group.project.cursusonlinecoursemanagement.user.domain.entity.Status;
import group.project.cursusonlinecoursemanagement.user.domain.entity.User;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GetInstructorResponse {

    private static ModelMapper modelMapper = new ModelMapper();

    UUID userId;
    String fullName;
    String email;
    String phone;
    Status status;
    boolean verify;
    List<Role> roles;
    List<EnrollmentResponse> enrollmentsResponse;

    List<GetCourseResponse> courses;


    public static GetInstructorResponse convertEntityToResponse(User user) {
        GetInstructorResponse getInstructorResponse = new GetInstructorResponse();

        getInstructorResponse.setUserId(user.getUserId());
        getInstructorResponse.setFullName(user.getFullName());
        getInstructorResponse.setEmail(user.getEmail());
        getInstructorResponse.setPhone(user.getPhone());
        getInstructorResponse.setStatus(user.getStatus());
        getInstructorResponse.setVerify(user.isVerify());
        getInstructorResponse.setRoles(user.getRoles().stream().toList());
        getInstructorResponse.setEnrollmentsResponse(
                user.getEnrollments().stream()
                        .map(EnrollmentResponse::convertEntityToResponse)
                        .collect(Collectors.toList())
        );
        getInstructorResponse.setCourses(user.getCourseList().stream().map(GetCourseResponse::convertEntityToResponseBasis).collect(Collectors.toList()));

        return getInstructorResponse;
    }
}
