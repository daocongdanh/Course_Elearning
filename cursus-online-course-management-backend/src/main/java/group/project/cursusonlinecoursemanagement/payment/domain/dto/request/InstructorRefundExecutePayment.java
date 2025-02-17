package group.project.cursusonlinecoursemanagement.payment.domain.dto.request;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InstructorRefundExecutePayment {
    String paymentId;
    String payerId;
    UUID paymentRequestId;
}
