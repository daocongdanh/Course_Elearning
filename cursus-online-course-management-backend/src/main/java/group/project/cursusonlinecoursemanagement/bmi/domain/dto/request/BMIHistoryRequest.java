package group.project.cursusonlinecoursemanagement.bmi.domain.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BMIHistoryRequest {
    private Double weight;
    private Double height;
    private UUID userId;
}
