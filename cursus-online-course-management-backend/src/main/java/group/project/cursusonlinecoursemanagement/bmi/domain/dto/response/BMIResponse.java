package group.project.cursusonlinecoursemanagement.bmi.domain.dto.response;

import group.project.cursusonlinecoursemanagement.bmi.domain.entity.BMI;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BMIResponse {
    private Long bmiId;
    private Double minBmi;
    private Double maxBmi;
    private String category;
    private String advice;

    public static BMIResponse convertEntityToResponse(BMI bmi){
        return BMIResponse.builder()
                .bmiId(bmi.getBmiId())
                .minBmi(bmi.getMinBmi())
                .maxBmi(bmi.getMaxBmi())
                .category(bmi.getCategory())
                .advice(bmi.getAdvice())
                .build();
    }
}
