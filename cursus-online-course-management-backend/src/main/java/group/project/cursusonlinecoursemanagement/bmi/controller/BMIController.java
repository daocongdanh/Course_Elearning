package group.project.cursusonlinecoursemanagement.bmi.controller;

import group.project.cursusonlinecoursemanagement.bmi.domain.dto.request.BMIRequest;
import group.project.cursusonlinecoursemanagement.bmi.domain.dto.response.BMIResponse;
import group.project.cursusonlinecoursemanagement.bmi.service.BMIService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bmi")
@RequiredArgsConstructor
public class BMIController {
    private final BMIService bmiService;

    @PostMapping("")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<BMIResponse> createBMI(@Valid @RequestBody BMIRequest bmiRequest){
        return new ResponseEntity<>(bmiService.createBMI(bmiRequest), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<BMIResponse> getBMIById(@PathVariable Long id){
        return new ResponseEntity<>(bmiService.getBMIById(id), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<BMIResponse> updateBMI(@PathVariable Long id, @Valid @RequestBody BMIRequest bmiRequest){
        return new ResponseEntity<>(bmiService.updateBMI(id, bmiRequest), HttpStatus.OK);
    }

    @GetMapping("")
    @PreAuthorize("hasRole('ROLE_STUDENT') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<BMIResponse>> getAllBMIs(){
        return new ResponseEntity<>(bmiService.getAllBMIs(), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteBMI(@PathVariable Long id){
        bmiService.deleteBMI(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/searchByBmi/{bmi}")
    @PreAuthorize("hasRole('ROLE_STUDENT') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<BMIResponse> searchByBmi(@PathVariable Double bmi){
        return new ResponseEntity<>(bmiService.searchByBmi(bmi), HttpStatus.OK);
    }

}
