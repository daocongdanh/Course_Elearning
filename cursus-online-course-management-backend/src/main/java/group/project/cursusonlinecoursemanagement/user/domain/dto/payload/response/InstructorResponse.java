package group.project.cursusonlinecoursemanagement.user.domain.dto.payload.response;

import group.project.cursusonlinecoursemanagement.course.domain.dto.payload.response.EnrollmentResponse;
import group.project.cursusonlinecoursemanagement.course.domain.entity.Enrollment;
import group.project.cursusonlinecoursemanagement.user.domain.entity.Instructor;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InstructorResponse {
    UUID userId;
    String introduction;
    String professionalExperience;
    String paypalID;
    Double walletBalance;
    LocalDateTime joinDate;

    public static InstructorResponse convertEntityToResponse(Instructor instructor) {
        InstructorResponse instructorResponse = new InstructorResponse();
        instructorResponse.setUserId(instructor.getUserId());
        instructorResponse.setIntroduction(instructor.getIntroduction());
        instructorResponse.setProfessionalExperience(instructor.getProfessionalExperience());
        instructorResponse.setPaypalID(instructor.getPaypalID());
        instructorResponse.setWalletBalance(instructor.getWalletBalance());
        instructorResponse.setJoinDate(instructor.getJoinDate());
        return instructorResponse;
    }
}
