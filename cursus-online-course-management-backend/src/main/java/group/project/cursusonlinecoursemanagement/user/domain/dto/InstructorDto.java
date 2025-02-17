package group.project.cursusonlinecoursemanagement.user.domain.dto;

import group.project.cursusonlinecoursemanagement.user.domain.dto.payload.response.InstructorResponse;
import group.project.cursusonlinecoursemanagement.user.domain.entity.Instructor;
import group.project.cursusonlinecoursemanagement.user.domain.entity.Status;
import group.project.cursusonlinecoursemanagement.user.domain.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InstructorDto {
    private static ModelMapper modelMapper = new ModelMapper();

    UUID userId;

    String email;

    String fullName;

    String phone;

    Status status;

    String avatarImageUrl;

    String introduction;

    String professionalExperience;

    String paypalID;

    Double walletBalance = 0.0;

    LocalDateTime joinDate;

    public static InstructorDto convertEntityToDto(Instructor instructor) {

        InstructorDto instructorDto = new InstructorDto();
        instructorDto.setUserId(instructor.getUserId());
        instructorDto.setEmail(instructor.getUser().getEmail());
        instructorDto.setFullName(instructor.getUser().getFullName());
        instructorDto.setPhone(instructor.getUser().getPhone());
        instructorDto.setStatus(instructor.getUser().getStatus());
        instructorDto.setJoinDate(instructor.getJoinDate());
        instructorDto.setIntroduction(instructor.getIntroduction());
        instructorDto.setProfessionalExperience(instructor.getProfessionalExperience());
        instructorDto.setPaypalID(instructor.getPaypalID());
        instructorDto.setWalletBalance(instructor.getWalletBalance());
        instructorDto.setJoinDate(instructor.getJoinDate());

        return instructorDto;
    }

    public static Instructor convertDtoToEntity(InstructorDto instructorDto) {
        return modelMapper.map(instructorDto, Instructor.class);
    }
}
