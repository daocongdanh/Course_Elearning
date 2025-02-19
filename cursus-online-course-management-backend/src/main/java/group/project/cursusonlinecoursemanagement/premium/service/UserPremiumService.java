package group.project.cursusonlinecoursemanagement.premium.service;

import group.project.cursusonlinecoursemanagement.premium.domain.dto.response.UserPremiumResponse;

import java.math.BigDecimal;
import java.util.UUID;

public interface UserPremiumService {
    UserPremiumResponse createUserPremium(UUID userId, BigDecimal totalPrice, int durationMonths);

    UserPremiumResponse getUserPremiumByUserId(UUID userId);
}
