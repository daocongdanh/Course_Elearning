package group.project.cursusonlinecoursemanagement.payment.repository;

import group.project.cursusonlinecoursemanagement.payment.domain.entity.PaymentRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface PaymentRequestRepository extends JpaRepository<PaymentRequest, UUID> {
}
