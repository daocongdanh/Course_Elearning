package group.project.cursusonlinecoursemanagement.premium.service.impl;

import group.project.cursusonlinecoursemanagement.premium.domain.dto.request.PremiumPackageRequest;
import group.project.cursusonlinecoursemanagement.premium.domain.dto.response.PremiumPackageResponse;
import group.project.cursusonlinecoursemanagement.premium.domain.entity.PremiumPackage;
import group.project.cursusonlinecoursemanagement.premium.repository.PremiumPackageRepository;
import group.project.cursusonlinecoursemanagement.premium.service.PremiumPackageService;
import group.project.cursusonlinecoursemanagement.shared.exception.handler.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PremiumPackageServiceImpl implements PremiumPackageService {
    private final PremiumPackageRepository premiumPackageRepository;
    @Override
    @Transactional
    public PremiumPackageResponse createPremiumPackage(PremiumPackageRequest premiumPackageRequest) {
        PremiumPackage premiumPackage = PremiumPackage.builder()
                .packageName(premiumPackageRequest.getPackageName())
                .durationMonths(premiumPackageRequest.getDurationMonths())
                .price(premiumPackageRequest.getPrice())
                .description(premiumPackageRequest.getDescription())
                .build();
        premiumPackageRepository.save(premiumPackage);
        return PremiumPackageResponse.convertEntityToResponse(premiumPackage);
    }

    @Override
    public PremiumPackageResponse getPremiumPackageById(Long id) {
        PremiumPackage premiumPackage = premiumPackageRepository.findById(id).
                orElseThrow(() -> new ResourceNotFoundException(
                        "Premium", "id", "Premium Package not found with id " + id));
        return PremiumPackageResponse.convertEntityToResponse(premiumPackage);
    }

    @Override
    @Transactional
    public PremiumPackageResponse updatePremiumPackage(Long id,
                                                       PremiumPackageRequest premiumPackageRequest) {
        PremiumPackage premiumPackage = premiumPackageRepository.findById(id).
                orElseThrow(() -> new ResourceNotFoundException(
                        "Premium", "id", "Premium Package not found with id " + id));

        premiumPackage.setPackageName(premiumPackageRequest.getPackageName());
        premiumPackage.setDurationMonths(premiumPackageRequest.getDurationMonths());
        premiumPackage.setPrice(premiumPackageRequest.getPrice());
        premiumPackage.setDescription(premiumPackageRequest.getDescription());

        return PremiumPackageResponse.convertEntityToResponse(premiumPackage);
    }

    @Override
    public List<PremiumPackageResponse> getAllPremiumPackages() {
        return premiumPackageRepository.findAll()
                .stream()
                .map(PremiumPackageResponse::convertEntityToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deletePremiumPackage(Long id) {
        PremiumPackage premiumPackage = premiumPackageRepository.findById(id).
                orElseThrow(() -> new ResourceNotFoundException(
                        "Premium", "id", "Premium Package not found with id " + id));
        premiumPackageRepository.delete(premiumPackage);
    }
}
