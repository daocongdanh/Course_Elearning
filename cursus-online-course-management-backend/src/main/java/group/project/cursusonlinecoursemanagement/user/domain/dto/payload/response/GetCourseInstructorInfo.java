package group.project.cursusonlinecoursemanagement.user.domain.dto.payload.response;

import group.project.cursusonlinecoursemanagement.user.domain.entity.User;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GetCourseInstructorInfo {
    UUID userId;
    String fullName;
    String introduction;
    String email;
    String avatarImageUrl;
    String professionalExperience;
    LocalDateTime joinDate;

    public static GetCourseInstructorInfo convertEntityToResponse(User instructor) {
        GetCourseInstructorInfo instructorResponse = new GetCourseInstructorInfo();
        instructorResponse.setUserId(instructor.getUserId());
        instructorResponse.setFullName(instructor.getFullName());
        instructorResponse.setIntroduction(instructor.getInstructor().getIntroduction());
        instructorResponse.setProfessionalExperience(instructor.getInstructor().getProfessionalExperience());
        instructorResponse.setJoinDate(instructor.getJoinDate());
        instructorResponse.setEmail(instructor.getEmail());
        instructorResponse.setAvatarImageUrl(instructor.getAvatarImageKey());
        return instructorResponse;
    }
}
