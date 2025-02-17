package group.project.cursusonlinecoursemanagement.course.domain.dto.payload.response;

import group.project.cursusonlinecoursemanagement.course.domain.entity.Enrollment;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GetUserOwnedCourseResponse {
    UUID courseId;
    String courseTitle;
    Double userCourseProgress;
    LocalDateTime enrolledDate;
    Double courseEnrolledPrice;

    public static GetUserOwnedCourseResponse convertEntityToResponse(Enrollment enrollment) {
        GetUserOwnedCourseResponse userOwnedCourseResponse = new GetUserOwnedCourseResponse();
        userOwnedCourseResponse.setCourseId(enrollment.getCourse().getCourseId());
        userOwnedCourseResponse.setCourseTitle(enrollment.getCourse().getCourseTitle());
        userOwnedCourseResponse.setEnrolledDate(enrollment.getEnrollmentDate());
        userOwnedCourseResponse.setCourseEnrolledPrice(enrollment.getEnrollmentPrice());
        return userOwnedCourseResponse;
    }
}
