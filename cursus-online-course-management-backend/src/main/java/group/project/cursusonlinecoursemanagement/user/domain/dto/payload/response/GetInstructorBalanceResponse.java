package group.project.cursusonlinecoursemanagement.user.domain.dto.payload.response;

import group.project.cursusonlinecoursemanagement.user.domain.entity.Instructor;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.modelmapper.ModelMapper;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GetInstructorBalanceResponse {

    private static ModelMapper modelMapper = new ModelMapper();

    Double balance;
    String currency;
    String paypalId;

    public GetInstructorBalanceResponse convertEntityToDto(Instructor instructor) {
        return modelMapper.map(instructor, GetInstructorBalanceResponse.class);
    }
}
