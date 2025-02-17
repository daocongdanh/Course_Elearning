package group.project.cursusonlinecoursemanagement.bmi.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BMIHistoryStatistics {
    private LocalDate day;
    private Double weight;
    private Double height;
    private Double bmi;
}
