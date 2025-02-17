package group.project.cursusonlinecoursemanagement.bmi.domain.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BMIRequest {
    private Double minBmi;
    private Double maxBmi;
    private String category;
    private String advice;
}
