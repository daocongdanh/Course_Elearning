package group.project.cursusonlinecoursemanagement.premium.service;

import group.project.cursusonlinecoursemanagement.premium.domain.dto.request.PremiumPackageRequest;
import group.project.cursusonlinecoursemanagement.premium.domain.dto.response.PremiumPackageResponse;

import java.util.List;

public interface PremiumPackageService {
    PremiumPackageResponse createPremiumPackage(PremiumPackageRequest premiumPackageRequest);
    PremiumPackageResponse getPremiumPackageById(Long id);
    PremiumPackageResponse updatePremiumPackage(Long id, PremiumPackageRequest premiumPackageRequest);
    List<PremiumPackageResponse> getAllPremiumPackages();
    void deletePremiumPackage(Long id);
}
