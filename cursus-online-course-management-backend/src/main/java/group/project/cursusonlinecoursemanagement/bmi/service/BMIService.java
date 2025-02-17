package group.project.cursusonlinecoursemanagement.bmi.service;

import group.project.cursusonlinecoursemanagement.bmi.domain.dto.request.BMIRequest;
import group.project.cursusonlinecoursemanagement.bmi.domain.dto.response.BMIResponse;

import java.util.List;

public interface BMIService {
    BMIResponse createBMI(BMIRequest bmiRequest);
    BMIResponse getBMIById(Long id);
    BMIResponse updateBMI(Long id, BMIRequest bmiRequest);
    List<BMIResponse> getAllBMIs();
    void deleteBMI(Long id);
    BMIResponse searchByBmi(Double bmi);
}
