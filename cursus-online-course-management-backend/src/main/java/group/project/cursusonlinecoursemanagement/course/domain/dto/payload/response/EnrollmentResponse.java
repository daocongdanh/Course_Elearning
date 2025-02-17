package group.project.cursusonlinecoursemanagement.course.domain.dto.payload.response;

import group.project.cursusonlinecoursemanagement.course.domain.entity.Enrollment;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EnrollmentResponse {
    private static ModelMapper modelMapper = new ModelMapper();
    long enrollmentId;
    LocalDateTime enrollmentDate;
    String userName;
    String courseName;
    Double enrollmentPrice;
    List<UserLessonProgressResponse> lessonProgressList;

    public static EnrollmentResponse convertEntityToResponse(Enrollment enrollment) {
        EnrollmentResponse enrollmentResponse = new EnrollmentResponse();
        enrollmentResponse.setEnrollmentId(enrollment.getEnrollmentId());
        enrollmentResponse.setEnrollmentDate(enrollment.getEnrollmentDate());
        enrollmentResponse.setUserName(enrollment.getUser().getFullName());
        enrollmentResponse.setCourseName(enrollment.getCourse().getCourseTitle());
        enrollmentResponse.setEnrollmentPrice(enrollment.getEnrollmentPrice());
        return enrollmentResponse;
    }
}
