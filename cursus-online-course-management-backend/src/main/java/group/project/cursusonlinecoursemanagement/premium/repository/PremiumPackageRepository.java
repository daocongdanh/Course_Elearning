package group.project.cursusonlinecoursemanagement.premium.repository;

import group.project.cursusonlinecoursemanagement.premium.domain.entity.PremiumPackage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PremiumPackageRepository extends JpaRepository<PremiumPackage, Long> {
}
