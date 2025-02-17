package group.project.cursusonlinecoursemanagement.payment.domain.entity;

import group.project.cursusonlinecoursemanagement.user.domain.entity.Instructor;
import group.project.cursusonlinecoursemanagement.user.domain.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "payment_request"
)
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "payment_request_id", nullable = false)
    private UUID paymentRequestId;

    @Column(name = "amount", nullable = false)
    private Double amount;

    @CreatedDate
    @Column(name = "request_date",updatable = false)
    private LocalDateTime requestDate;

    @Column(name = "receive_paypal_id")
    private String receivePaypalId;

    @CreatedBy
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @ToString.Exclude
    private Instructor instructor;

    @Column(name = "is_paid", nullable = false)
    private boolean isPaid;
}
