package group.project.cursusonlinecoursemanagement.bmi.service.impl;

import group.project.cursusonlinecoursemanagement.bmi.domain.dto.request.BMIHistoryRequest;
import group.project.cursusonlinecoursemanagement.bmi.domain.dto.response.BMIHistoryResponse;
import group.project.cursusonlinecoursemanagement.bmi.domain.dto.response.BMIHistoryStatistics;
import group.project.cursusonlinecoursemanagement.bmi.domain.entity.BMIHistory;
import group.project.cursusonlinecoursemanagement.bmi.repository.BMIHistoryRepository;
import group.project.cursusonlinecoursemanagement.bmi.service.BMIHistoryService;
import group.project.cursusonlinecoursemanagement.shared.exception.handler.ResourceNotFoundException;
import group.project.cursusonlinecoursemanagement.user.domain.entity.User;
import group.project.cursusonlinecoursemanagement.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BMIHistoryServiceImpl implements BMIHistoryService {
    private final BMIHistoryRepository bmiHistoryRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public BMIHistoryResponse createBMIHistory(BMIHistoryRequest bmiHistoryRequest) {
        UUID userId = bmiHistoryRequest.getUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId.toString()));

        BMIHistory bmiHistory = BMIHistory.builder()
                .weight(bmiHistoryRequest.getWeight())
                .height(bmiHistoryRequest.getHeight())
                .bmi(calculatorBMI(bmiHistoryRequest.getWeight(), bmiHistoryRequest.getHeight()))
                .recordedAt(LocalDateTime.now())
                .user(user)
                .build();

        bmiHistoryRepository.save(bmiHistory);

        return BMIHistoryResponse.convertEntityToResponse(bmiHistory);
    }

    @Override
    public BMIHistoryResponse getBMICurrent(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId.toString()));
        return bmiHistoryRepository.findTopByUserOrderByRecordedAtDesc(user)
                .map(BMIHistoryResponse::convertEntityToResponse)
                .orElse(null);
    }

    @Override
    public List<BMIHistoryStatistics> getBMIDaily(int year, int month, UUID userId) {
        List<Object[]> results = bmiHistoryRepository.findDailyBMI(year, month, userId);
        return results.stream().map(row -> BMIHistoryStatistics.builder()
                .day(((LocalDateTime) row[0]).toLocalDate())
                .weight((Double) row[1])
                .height((Double) row[2])
                .bmi((Double) row[3])
                .build())
                .toList();
    }

    private Double calculatorBMI(Double weight, Double height){
        return weight / (height * height);
    }
}
