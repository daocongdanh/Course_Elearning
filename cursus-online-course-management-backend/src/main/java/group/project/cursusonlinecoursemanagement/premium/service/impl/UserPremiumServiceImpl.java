package group.project.cursusonlinecoursemanagement.premium.service.impl;

import group.project.cursusonlinecoursemanagement.premium.domain.dto.response.UserPremiumResponse;
import group.project.cursusonlinecoursemanagement.premium.domain.entity.UserPremium;
import group.project.cursusonlinecoursemanagement.premium.repository.UserPremiumRepository;
import group.project.cursusonlinecoursemanagement.premium.service.UserPremiumService;
import group.project.cursusonlinecoursemanagement.shared.exception.handler.ResourceNotFoundException;
import group.project.cursusonlinecoursemanagement.user.domain.entity.User;
import group.project.cursusonlinecoursemanagement.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserPremiumServiceImpl implements UserPremiumService {
    private final UserPremiumRepository userPremiumRepository;
    private final UserRepository userRepository;
    @Override
    @Transactional
    public UserPremiumResponse createUserPremium(UUID userId, BigDecimal totalPrice, int durationMonths) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId.toString()));

        Optional<UserPremium> optionalUserPremium = userPremiumRepository.findByUser(user);
        UserPremium userPremium = null;
        if(optionalUserPremium.isEmpty()){
            userPremium = UserPremium.builder()
                    .user(user)
                    .totalPrice(totalPrice)
                    .startDate(LocalDate.now())
                    .endDate(LocalDate.now().plusMonths(durationMonths))
                    .build();
        }
        else{
            UserPremium premium = optionalUserPremium.get();
            premium.setTotalPrice(totalPrice);
            if(premium.getEndDate().isBefore(LocalDate.now())){
                premium.setStartDate(LocalDate.now());
                premium.setEndDate(LocalDate.now().plusMonths(durationMonths));
            }
            else{
                premium.setEndDate(premium.getEndDate().plusMonths(durationMonths));
            }
            userPremium = premium;
        }
        if(userPremium != null){
            userPremiumRepository.save(userPremium);
            return UserPremiumResponse.convertEntityToResponse(userPremium);
        }
        return null;
    }

    @Override
    public UserPremiumResponse getUserPremiumByUserId(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId.toString()));

        Optional<UserPremium> optionalUserPremium = userPremiumRepository.findByUser(user);
        return optionalUserPremium.map(UserPremiumResponse::convertEntityToResponse).orElse(null);
    }
}
