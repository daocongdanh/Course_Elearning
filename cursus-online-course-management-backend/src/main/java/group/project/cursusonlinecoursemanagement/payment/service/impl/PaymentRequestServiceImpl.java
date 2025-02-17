package group.project.cursusonlinecoursemanagement.payment.service.impl;

import group.project.cursusonlinecoursemanagement.payment.domain.dto.request.CreatePaymentRequest;
import group.project.cursusonlinecoursemanagement.payment.domain.dto.response.GetPaymentRequestResponse;
import group.project.cursusonlinecoursemanagement.payment.domain.entity.PaymentRequest;
import group.project.cursusonlinecoursemanagement.payment.repository.PaymentRequestRepository;
import group.project.cursusonlinecoursemanagement.payment.service.PaymentRequestService;
import group.project.cursusonlinecoursemanagement.shared.exception.handler.AppExceptionHandler;
import group.project.cursusonlinecoursemanagement.shared.exception.handler.ResourceNotFoundException;
import group.project.cursusonlinecoursemanagement.user.domain.dto.payload.response.InstructorResponse;
import group.project.cursusonlinecoursemanagement.user.domain.entity.Instructor;
import group.project.cursusonlinecoursemanagement.user.domain.entity.User;
import group.project.cursusonlinecoursemanagement.user.repository.InstructorRepository;
import group.project.cursusonlinecoursemanagement.user.service.InstructorService;
import group.project.cursusonlinecoursemanagement.user.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PaymentRequestServiceImpl implements PaymentRequestService {
    private final PaymentRequestRepository paymentRequestRepository;
    private final InstructorRepository instructorRepository;

    public PaymentRequestServiceImpl(PaymentRequestRepository paymentRequestRepository, InstructorRepository instructorRepository) {
        this.paymentRequestRepository = paymentRequestRepository;
        this.instructorRepository = instructorRepository;
    }

    @Override
    public List<GetPaymentRequestResponse> getAllPaymentRequest() {
        try {
            List<PaymentRequest> paymentRequestList = paymentRequestRepository.findAll();

            return paymentRequestList.stream()
                    .map(GetPaymentRequestResponse::convertEntityToResponse)
                    .collect(Collectors.toList());
        }catch (Exception e){
            throw new AppExceptionHandler("Error retrieving all payment requests");
        }

    }

    @Override
    public GetPaymentRequestResponse createPaymentRequest(String email, CreatePaymentRequest createPaymentRequest) {
        try {
            PaymentRequest paymentRequest = new PaymentRequest();
            paymentRequest.setRequestDate(LocalDateTime.now());
            paymentRequest.setAmount(createPaymentRequest.getAmount());
            Instructor instructor = instructorRepository.findByUser_Email(email).orElseThrow(() ->
                    new ResourceNotFoundException("Instructor", "email", email));
            if (instructor.getWalletBalance() < createPaymentRequest.getAmount()) {
                throw new AppExceptionHandler("Not enough money in wallet");
            }
            paymentRequest.setInstructor(instructor);
            paymentRequest.setReceivePaypalId(createPaymentRequest.getReceivePaypalId());
            paymentRequest.setPaid(false);
            //Lưu vào database
            instructor.setPaypalID(createPaymentRequest.getPaypalPaymentId());
            instructorRepository.save(instructor);
            paymentRequestRepository.save(paymentRequest);
            //Trả về true thông tin nếu thành công
            return GetPaymentRequestResponse.convertEntityToResponse(paymentRequest);
        } catch (Exception e) {
            throw new AppExceptionHandler("Error create payment request");
        }
    }

    @Override
    public GetPaymentRequestResponse getPaymentRequestById(UUID uuid) {
        PaymentRequest paymentRequest = paymentRequestRepository.findById(uuid).orElseThrow(() ->
                new ResourceNotFoundException("Payment Request", "ID", uuid.toString()));

        return  GetPaymentRequestResponse.convertEntityToResponse(paymentRequest);
    }

    @Override
    public GetPaymentRequestResponse isDone(UUID uuid) {
        try {
            PaymentRequest paymentRequest = paymentRequestRepository.findById(uuid).orElseThrow(() ->
                    new ResourceNotFoundException("Payment Request", "ID", uuid.toString()));

            paymentRequest.setPaid(true);
            Instructor instructor = paymentRequest.getInstructor();
            instructor.setWalletBalance(instructor.getWalletBalance()-paymentRequest.getAmount());
            instructorRepository.save(instructor);
            //Lưu vào database
            paymentRequestRepository.save(paymentRequest);
            //Trả về true thông tin nếu thành công
            return GetPaymentRequestResponse.convertEntityToResponse(paymentRequest);
        } catch (Exception e) {
            throw new AppExceptionHandler("Error update status of payment request " + e.getMessage());
        }
    }

    @Override
    public List<GetPaymentRequestResponse> getAllPaymentRequestIsDone() {
        try {
            List<PaymentRequest> paymentRequestList = paymentRequestRepository
                    .findAll();

            return paymentRequestList.stream()
                    .filter(PaymentRequest::isPaid)
                    .map(GetPaymentRequestResponse::convertEntityToResponse)
                    .collect(Collectors.toList());
        }catch (Exception e){
            throw new AppExceptionHandler("Error retrieving payment requests");
        }
    }

    @Override
    public List<GetPaymentRequestResponse> getAllPaymentRequestIsNotDone() {
        try {
            List<PaymentRequest> paymentRequestList = paymentRequestRepository
                    .findAll();

            return paymentRequestList.stream()
                    .filter(paymentRequest -> !paymentRequest.isPaid())
                    .map(GetPaymentRequestResponse::convertEntityToResponse)
                    .collect(Collectors.toList());

        }catch (Exception e){
            throw new AppExceptionHandler(e.getMessage());
        }
    }
}
