package group.project.cursusonlinecoursemanagement.bmi.controller;

import group.project.cursusonlinecoursemanagement.bmi.domain.dto.request.BMIHistoryRequest;
import group.project.cursusonlinecoursemanagement.bmi.domain.dto.response.BMIHistoryResponse;
import group.project.cursusonlinecoursemanagement.bmi.domain.dto.response.BMIHistoryStatistics;
import group.project.cursusonlinecoursemanagement.bmi.service.BMIHistoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/bmi-history")
@RequiredArgsConstructor
public class BMIHistoryController {
    private final BMIHistoryService bmiHistoryService;


    @PostMapping("")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public ResponseEntity<BMIHistoryResponse> createBMIHistory(@Valid @RequestBody BMIHistoryRequest bmiHistoryRequest){
        return new ResponseEntity<>(bmiHistoryService.createBMIHistory(bmiHistoryRequest), HttpStatus.CREATED);
    }

    @GetMapping("/current/{userId}")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public ResponseEntity<BMIHistoryResponse> getBMICurrent(@PathVariable UUID userId){
        return new ResponseEntity<>(bmiHistoryService.getBMICurrent(userId), HttpStatus.OK);
    }

    @GetMapping("/daily/{year}/{month}/{userId}")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public ResponseEntity<List<BMIHistoryStatistics>> getBMIDaily(@PathVariable int year, @PathVariable int month, @PathVariable UUID userId){
        return new ResponseEntity<>(bmiHistoryService.getBMIDaily(year, month, userId), HttpStatus.OK);
    }
}
