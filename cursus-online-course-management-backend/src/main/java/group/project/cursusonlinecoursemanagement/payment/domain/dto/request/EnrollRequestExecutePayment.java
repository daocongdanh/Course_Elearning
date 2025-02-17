package group.project.cursusonlinecoursemanagement.payment.domain.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EnrollRequestExecutePayment {
    String paymentId;
    String payerId;
    UUID courseId;
}
