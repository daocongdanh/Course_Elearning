package group.project.cursusonlinecoursemanagement.bmi.service;

import group.project.cursusonlinecoursemanagement.bmi.domain.dto.request.BMIHistoryRequest;
import group.project.cursusonlinecoursemanagement.bmi.domain.dto.response.BMIHistoryResponse;
import group.project.cursusonlinecoursemanagement.bmi.domain.dto.response.BMIHistoryStatistics;

import java.util.List;
import java.util.UUID;

public interface BMIHistoryService {
    BMIHistoryResponse createBMIHistory(BMIHistoryRequest bmiHistoryRequest);
    BMIHistoryResponse getBMICurrent(UUID userId);
    List<BMIHistoryStatistics> getBMIDaily(int year, int month, UUID userId);
}
