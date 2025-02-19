package group.project.cursusonlinecoursemanagement.premium.domain.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PremiumPackageRequest {
    private String packageName;
    private int durationMonths;
    private BigDecimal price;
    private String description;
}
