package group.project.cursusonlinecoursemanagement.course.domain.dto.payload.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RefundPaymentRequest {
     String returnUrl;
     String cancelUrl;
}
