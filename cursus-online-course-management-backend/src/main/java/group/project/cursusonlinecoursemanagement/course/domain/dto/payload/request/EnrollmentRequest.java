package group.project.cursusonlinecoursemanagement.course.domain.dto.payload.request;

import group.project.cursusonlinecoursemanagement.course.domain.dto.payload.response.EnrollmentResponse;
import group.project.cursusonlinecoursemanagement.course.domain.entity.Course;
import group.project.cursusonlinecoursemanagement.course.domain.entity.Enrollment;
import group.project.cursusonlinecoursemanagement.user.domain.entity.User;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EnrollmentRequest {
    private static ModelMapper modelMapper = new ModelMapper();
    UUID userId;
    UUID courseId;

}
