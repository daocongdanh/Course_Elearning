package group.project.cursusonlinecoursemanagement.payment.service;

import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import group.project.cursusonlinecoursemanagement.course.domain.dto.payload.response.EnrollmentResponse;
import group.project.cursusonlinecoursemanagement.payment.domain.dto.request.EnrollRequestPayment;
import group.project.cursusonlinecoursemanagement.payment.domain.dto.response.GetPaymentRequestResponse;

import java.util.UUID;

public interface PaymentService {
    Payment createPayment(Double aDouble, String currency, String method, String sale, String description, String cancelUrl, String successUrl, String payeeId) throws PayPalRESTException;
    
    Payment executePayment(String paymentId, String payerId) throws PayPalRESTException;

    Payment createEnrollRequestPayment(EnrollRequestPayment enrollRequestPayment) throws PayPalRESTException;

    Payment createPaymentWithPaymentRequest(UUID paymentRequestId, String returnUrl, String cancelUrl) throws PayPalRESTException;
    EnrollmentResponse executePaymentRequest(
            String paymentId,
            String payerId,
            String email,
            UUID courseId
    ) throws PayPalRESTException;

    GetPaymentRequestResponse executeRefundPayment(
            String paymentId,
            String payerId,
            UUID paymentRequestId
    ) throws PayPalRESTException;
}
