package group.project.cursusonlinecoursemanagement.bmi.domain.dto.response;

import group.project.cursusonlinecoursemanagement.bmi.domain.entity.BMIHistory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BMIHistoryResponse {
    private Long bmiHistoryId;
    private Double weight;
    private Double height;
    private Double bmi;
    private LocalDateTime recordedAt;
    private UUID userId;

    public static BMIHistoryResponse convertEntityToResponse(BMIHistory bmiHistory){
        return BMIHistoryResponse.builder()
                .bmiHistoryId(bmiHistory.getBmiHistoryId())
                .weight(bmiHistory.getWeight())
                .height(bmiHistory.getHeight())
                .bmi(bmiHistory.getBmi())
                .recordedAt(bmiHistory.getRecordedAt())
                .userId(bmiHistory.getUser().getUserId())
                .build();
    }
}
