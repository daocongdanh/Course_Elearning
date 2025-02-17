package group.project.cursusonlinecoursemanagement.payment.controller;

import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import group.project.cursusonlinecoursemanagement.course.domain.dto.payload.request.RefundPaymentRequest;
import group.project.cursusonlinecoursemanagement.payment.domain.dto.request.EnrollRequestExecutePayment;
import group.project.cursusonlinecoursemanagement.payment.domain.dto.request.EnrollRequestPayment;
import group.project.cursusonlinecoursemanagement.payment.domain.dto.request.InstructorRefundExecutePayment;
import group.project.cursusonlinecoursemanagement.payment.service.PaymentService;
import group.project.cursusonlinecoursemanagement.shared.permission.user.UserStatus;
import group.project.cursusonlinecoursemanagement.user.domain.entity.Status;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import com.paypal.api.payments.*;
import java.util.UUID;

@Tag(
        name = "CRUD REST APIs for Payment"
)
@RestController
@RequestMapping("/api/payment")
public class PaymentController {
    private final PaymentService paymentService;
    private final APIContext apiContext;
    public PaymentController(PaymentService paymentService, APIContext apiContext) {
        this.paymentService = paymentService;
        this.apiContext = apiContext;
    }

    @PreAuthorize("hasRole('ROLE_STUDENT')")
    @UserStatus(unAllowedStatuses = {Status.BLOCK_ROLE_STUDENT,Status.BLOCK_ACCOUNT})
    @PostMapping("/auth/createEnrollRequestPayment")
    @Operation(
            summary = "Create Enroll Request Payment REST API",
            description = "Create Enroll Request Payment REST API is used to create payment request for enrolling in a course"
    )
    @SecurityRequirement(
            name = "Bearer Authentication"
    )
    public ResponseEntity<?> createEnrollPayment(@RequestBody EnrollRequestPayment enrollRequestPayment) throws PayPalRESTException {
        Payment payment = paymentService.createEnrollRequestPayment(enrollRequestPayment);
        for (Links links : payment.getLinks()) {
            if (links.getRel().equals("approval_url")) {
                return new ResponseEntity<>(links.getHref(), HttpStatus.OK);
            }
        }
        return new ResponseEntity<>("No approval URL found", HttpStatus.BAD_REQUEST);
    }

    @PreAuthorize("hasRole('ROLE_STUDENT')")
    @UserStatus(unAllowedStatuses = {Status.BLOCK_ROLE_STUDENT,Status.BLOCK_ACCOUNT})
    @PostMapping("/auth/executeEnrollRequestPayment")
    @Operation(
            summary = "Execute Payment REST API",
            description = "Execute Payment REST API is used to execute payment request"
    )
    public ResponseEntity<?> executePayment(@RequestBody EnrollRequestExecutePayment executePayment) throws PayPalRESTException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return new ResponseEntity<>(
                paymentService.executePaymentRequest(executePayment.getPaymentId(), executePayment.getPayerId(), email, executePayment.getCourseId()),
                HttpStatus.OK
        );
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/auth/createInstructorRefundPayment/{id}")
    @Operation(
            summary = "Create Instructor Refund Payment REST API",
            description = "Create Instructor Refund Payment REST API is used to create refund payment request for Payment Request"
    )
    @SecurityRequirement(
            name = "Bearer Authentication"
    )
    public ResponseEntity<?> createInstructorRefundPayment(
            @PathVariable UUID id,
            @RequestBody RefundPaymentRequest refundPaymentRequest) throws PayPalRESTException {
        Payment payment = paymentService.createPaymentWithPaymentRequest(id, refundPaymentRequest.getReturnUrl(), refundPaymentRequest.getCancelUrl());
        for (Links links : payment.getLinks()) {
            if (links.getRel().equals("approval_url")) {
                return new ResponseEntity<>(links.getHref(), HttpStatus.OK);
            }
        }
        return new ResponseEntity<>("No approval URL found", HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/auth/executeInstructorRefundPayment")
    @Operation(
            summary = "Execute Payment REST API",
            description = "Execute Payment REST API is used to execute Instructor Refund Payment"
    )
    public ResponseEntity<?> executeInstructorRefundPayment(@RequestBody InstructorRefundExecutePayment executePayment) throws PayPalRESTException {
        return new ResponseEntity<>(
                paymentService.executeRefundPayment(executePayment.getPaymentId(), executePayment.getPayerId(), executePayment.getPaymentRequestId()),
                HttpStatus.OK
        );
    }
}
