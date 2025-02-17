package group.project.cursusonlinecoursemanagement.user.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
public class Instructor {
    @Id
    private UUID userId;
    
    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;
    
    @Column(columnDefinition = "TEXT")
    private String introduction;
    
    @Column(columnDefinition = "TEXT")
    private String professionalExperience;

    @Column(name = "paypal_id")
    private String paypalID;

    @Column(name = "wallet_balance")
    private Double walletBalance = 0.0;

    @CreatedDate
    @Column(name = "join_date")
    private LocalDateTime joinDate;
}
