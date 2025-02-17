package group.project.cursusonlinecoursemanagement.payment.domain.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreatePaymentRequest {
    Double amount;
    String paypalPaymentId;
    String receivePaypalId;
}
