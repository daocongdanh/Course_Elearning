package group.project.cursusonlinecoursemanagement.premium.controller;

import group.project.cursusonlinecoursemanagement.premium.domain.dto.request.PremiumPackageRequest;
import group.project.cursusonlinecoursemanagement.premium.domain.dto.response.PremiumPackageResponse;
import group.project.cursusonlinecoursemanagement.premium.service.PremiumPackageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/premium-package")
@RequiredArgsConstructor
public class PremiumPackageController {
    private final PremiumPackageService premiumPackageService;


    @PostMapping("")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<PremiumPackageResponse> createPremiumPackage(@Valid @RequestBody
                                                                PremiumPackageRequest premiumPackageRequest){
        return new ResponseEntity<>(premiumPackageService.createPremiumPackage(premiumPackageRequest), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<PremiumPackageResponse> updatePremiumPackage(@PathVariable Long id, @Valid @RequestBody
                                                                PremiumPackageRequest premiumPackageRequest){
        return new ResponseEntity<>(premiumPackageService.updatePremiumPackage(id, premiumPackageRequest), HttpStatus.OK);
    }

    @GetMapping("")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_STUDENT') or hasRole('ROLE_INSTRUCTOR')")
    public ResponseEntity<List<PremiumPackageResponse>> getAllPremiumPackages(){
        return new ResponseEntity<>(premiumPackageService.getAllPremiumPackages(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_STUDENT') or hasRole('ROLE_INSTRUCTOR')")
    public ResponseEntity<PremiumPackageResponse> getPremiumPackageById(@PathVariable Long id){
        return new ResponseEntity<>(premiumPackageService.getPremiumPackageById(id), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deletePremiumPackage(@PathVariable Long id){
        premiumPackageService.deletePremiumPackage(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
