package group.project.cursusonlinecoursemanagement.premium.domain.dto.response;

import group.project.cursusonlinecoursemanagement.premium.domain.entity.UserPremium;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPremiumResponse {
    private UUID userId;
    private BigDecimal totalPrice;
    private LocalDate startDate;
    private LocalDate endDate;

    public static UserPremiumResponse convertEntityToResponse(UserPremium userPremium) {
        return UserPremiumResponse.builder()
                .userId(userPremium.getUser().getUserId())
                .totalPrice(userPremium.getTotalPrice())
                .startDate(userPremium.getStartDate())
                .endDate(userPremium.getEndDate())
                .build();
    }
}
