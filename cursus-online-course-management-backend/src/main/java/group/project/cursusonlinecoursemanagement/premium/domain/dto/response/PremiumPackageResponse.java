package group.project.cursusonlinecoursemanagement.premium.domain.dto.response;

import group.project.cursusonlinecoursemanagement.premium.domain.entity.PremiumPackage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PremiumPackageResponse {
    private Long premiumPackageId;
    private String packageName;
    private int durationMonths;
    private BigDecimal price;
    private String description;

    public static PremiumPackageResponse convertEntityToResponse(PremiumPackage premiumPackage) {
        return PremiumPackageResponse.builder()
                .premiumPackageId(premiumPackage.getPremiumPackageId())
                .packageName(premiumPackage.getPackageName())
                .durationMonths(premiumPackage.getDurationMonths())
                .price(premiumPackage.getPrice())
                .description(premiumPackage.getDescription())
                .build();
    }
}
