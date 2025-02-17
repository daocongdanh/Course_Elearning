package group.project.cursusonlinecoursemanagement.payment.domain.dto.response;

import group.project.cursusonlinecoursemanagement.payment.domain.entity.PaymentRequest;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GetPaymentRequestResponse {
    UUID paymentRequestId;
    Double amount;
    LocalDateTime requestDate;
    String instructorPaypalId;
    boolean isPaid;
    String fullName;

    public static GetPaymentRequestResponse convertEntityToResponse(PaymentRequest paymentRequest) {
        GetPaymentRequestResponse getPaymentRequestResponse = new GetPaymentRequestResponse();

        getPaymentRequestResponse.setPaymentRequestId(paymentRequest.getPaymentRequestId());
        getPaymentRequestResponse.setFullName(paymentRequest.getInstructor().getUser().getFullName());
        getPaymentRequestResponse.setAmount(paymentRequest.getAmount());
        getPaymentRequestResponse.setRequestDate(paymentRequest.getRequestDate());
        getPaymentRequestResponse.setInstructorPaypalId(paymentRequest.getReceivePaypalId());
        getPaymentRequestResponse.setPaid(paymentRequest.isPaid());

        return getPaymentRequestResponse;
    }
}
