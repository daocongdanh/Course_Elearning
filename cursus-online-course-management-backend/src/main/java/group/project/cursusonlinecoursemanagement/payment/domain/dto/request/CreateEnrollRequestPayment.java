package group.project.cursusonlinecoursemanagement.payment.domain.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateEnrollRequestPayment {
    private UUID userId;
    private UUID courseId;
    private Double amount;
    private String description;
    private String title;
}
