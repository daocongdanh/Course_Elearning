package group.project.cursusonlinecoursemanagement.user.domain.dto.payload.response;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class CourseEnrollmentCountByDate {
    private LocalDateTime date;
    private long courseCount;
    private long enrollmentCount;
}
