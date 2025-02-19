package group.project.cursusonlinecoursemanagement.premium.domain.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreatePaymentPremium {
    private UUID userId;
    private BigDecimal totalPrice;
    private int durationMonths;
}
