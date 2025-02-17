package group.project.cursusonlinecoursemanagement.payment.domain.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EnrollRequestPayment {
    String cancelUrl;
    String returnUrl;
    String method;
    Double amount;
    String currency;
    String description;
    String payeeId;
}
