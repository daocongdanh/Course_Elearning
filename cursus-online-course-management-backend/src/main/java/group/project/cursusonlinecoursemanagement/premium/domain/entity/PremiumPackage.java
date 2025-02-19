package group.project.cursusonlinecoursemanagement.premium.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(
        name = "premium_package"
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PremiumPackage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "premium_package_id", nullable = false)
    private Long premiumPackageId;

    @Column(name = "package_name", nullable = false)
    private String packageName;

    @Column(name = "duration_months", nullable = false)
    private int durationMonths;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
}
