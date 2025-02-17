package group.project.cursusonlinecoursemanagement.payment.service;

import group.project.cursusonlinecoursemanagement.payment.domain.dto.request.CreatePaymentRequest;
import group.project.cursusonlinecoursemanagement.payment.domain.dto.response.GetPaymentRequestResponse;

import java.util.List;
import java.util.UUID;

public interface PaymentRequestService {
    List<GetPaymentRequestResponse> getAllPaymentRequest();
    GetPaymentRequestResponse createPaymentRequest(String email, CreatePaymentRequest createPaymentRequest);
    GetPaymentRequestResponse getPaymentRequestById(UUID uuid);
    GetPaymentRequestResponse isDone(UUID uuid);
    List<GetPaymentRequestResponse> getAllPaymentRequestIsDone();
    List<GetPaymentRequestResponse> getAllPaymentRequestIsNotDone();
}
