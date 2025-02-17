package group.project.cursusonlinecoursemanagement.bmi.service.impl;

import group.project.cursusonlinecoursemanagement.bmi.domain.dto.request.BMIRequest;
import group.project.cursusonlinecoursemanagement.bmi.domain.dto.response.BMIResponse;
import group.project.cursusonlinecoursemanagement.bmi.domain.entity.BMI;
import group.project.cursusonlinecoursemanagement.bmi.repository.BMIRepository;
import group.project.cursusonlinecoursemanagement.bmi.service.BMIService;
import group.project.cursusonlinecoursemanagement.shared.exception.handler.AppExceptionHandler;
import group.project.cursusonlinecoursemanagement.shared.exception.handler.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BMIServiceImpl implements BMIService {
    private final BMIRepository bmiRepository;

    @Override
    @Transactional
    public BMIResponse createBMI(BMIRequest bmiRequest) {
        Double minBmi = bmiRequest.getMinBmi();
        Double maxBmi = bmiRequest.getMaxBmi();

        Long count = bmiRepository.countOverlappingBMIs1(minBmi, maxBmi);
        if (count > 0) {
            throw new AppExceptionHandler("BMI conflict Min and Max");
        }

        BMI bmi = BMI.builder()
                .minBmi(minBmi)
                .maxBmi(maxBmi)
                .category(bmiRequest.getCategory())
                .advice(bmiRequest.getAdvice())
                .build();
        bmiRepository.save(bmi);
        return BMIResponse.convertEntityToResponse(bmi);
    }

    @Override
    public BMIResponse getBMIById(Long id) {
        BMI bmi = findById(id);
        return BMIResponse.convertEntityToResponse(bmi);
    }

    @Override
    @Transactional
    public BMIResponse updateBMI(Long id, BMIRequest bmiRequest) {
        BMI bmi = findById(id);

        Double minBmi = bmiRequest.getMinBmi();
        Double maxBmi = bmiRequest.getMaxBmi();

        Long count = bmiRepository.countOverlappingBMIs2(minBmi, maxBmi, id);
        if (count > 0) {
            throw new AppExceptionHandler("BMI conflict Min and Max");
        }

        bmi.setMinBmi(minBmi);
        bmi.setMaxBmi(maxBmi);
        bmi.setCategory(bmiRequest.getCategory());
        bmi.setAdvice(bmiRequest.getAdvice());

        bmiRepository.save(bmi);
        return BMIResponse.convertEntityToResponse(bmi);
    }

    @Override
    public List<BMIResponse> getAllBMIs() {
        return bmiRepository.findAll()
                .stream()
                .map(BMIResponse::convertEntityToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteBMI(Long id) {
        BMI bmi = findById(id);
        bmiRepository.delete(bmi);
    }

    @Override
    public BMIResponse searchByBmi(Double bmi) {
        Optional<BMI> optionalBMI = bmiRepository.findByBmiValue(bmi);
        if(optionalBMI.isEmpty())
            throw new ResourceNotFoundException("BMI", "bmi", "No result");
        return BMIResponse.convertEntityToResponse(optionalBMI.get());
    }

    private BMI findById(Long id){
        return bmiRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BMI", "bmiId", id.toString()));
    }

}
