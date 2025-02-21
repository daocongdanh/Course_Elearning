package group.project.cursusonlinecoursemanagement.payment.controller;

import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import group.project.cursusonlinecoursemanagement.course.domain.dto.payload.request.EnrollmentRequest;
import group.project.cursusonlinecoursemanagement.course.domain.dto.payload.request.RefundPaymentRequest;
import group.project.cursusonlinecoursemanagement.course.service.EnrollmentService;
import group.project.cursusonlinecoursemanagement.payment.domain.dto.request.CreateEnrollRequestPayment;
import group.project.cursusonlinecoursemanagement.payment.domain.dto.request.EnrollRequestExecutePayment;
import group.project.cursusonlinecoursemanagement.payment.domain.dto.request.EnrollRequestPayment;
import group.project.cursusonlinecoursemanagement.payment.domain.dto.request.InstructorRefundExecutePayment;
import group.project.cursusonlinecoursemanagement.payment.service.PaymentService;
import group.project.cursusonlinecoursemanagement.premium.domain.dto.request.CreatePaymentPremium;
import group.project.cursusonlinecoursemanagement.premium.service.UserPremiumService;
import group.project.cursusonlinecoursemanagement.shared.permission.user.UserStatus;
import group.project.cursusonlinecoursemanagement.user.domain.entity.Status;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import com.paypal.api.payments.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.UUID;

@Tag(
        name = "CRUD REST APIs for Payment"
)
@RestController
@RequestMapping("/api/payment")
public class PaymentController {
    private final PaymentService paymentService;
    private final APIContext apiContext;
    private final EnrollmentService enrollmentService;
    private final UserPremiumService userPremiumService;

    @Value("${domain.frontend}")
    private String domainFrontend;
    public PaymentController(PaymentService paymentService, APIContext apiContext,
                             EnrollmentService enrollmentService,
                             UserPremiumService userPremiumService) {
        this.paymentService = paymentService;
        this.apiContext = apiContext;
        this.enrollmentService = enrollmentService;
        this.userPremiumService = userPremiumService;
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


    @PreAuthorize("hasRole('ROLE_STUDENT')")
    @UserStatus(unAllowedStatuses = {Status.BLOCK_ROLE_STUDENT,Status.BLOCK_ACCOUNT})
    @PostMapping("/auth/create-payment")
    @Operation(
            summary = "Create Enroll Request Payment REST API",
            description = "Create Enroll Request Payment REST API is used to create payment request for enrolling in a course"
    )
    @SecurityRequirement(
            name = "Bearer Authentication"
    )
    public ResponseEntity<?> createEnrollRequestPayment(
            @RequestBody CreateEnrollRequestPayment createEnrollRequestPayment) {
        String url = paymentService.createEnrollRequestPayment(createEnrollRequestPayment);
        return new ResponseEntity<>(url, HttpStatus.OK);
    }
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    @UserStatus(unAllowedStatuses = {Status.BLOCK_ROLE_STUDENT,Status.BLOCK_ACCOUNT})
    @PostMapping("/auth/create-payment-premium")
    public ResponseEntity<?> createPremiumPayment(
            @RequestBody CreatePaymentPremium createPaymentPremium) {
        String url = paymentService.createPremiumPayment(createPaymentPremium);
        return new ResponseEntity<>(url, HttpStatus.OK);
    }

    @GetMapping("/success")
    public ResponseEntity<?> handlePaymentSuccess(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String type = request.getParameter("type");
        if(type.equals("enrollment")){
            UUID userId = UUID.fromString(request.getParameter("userId"));
            UUID courseId = UUID.fromString(request.getParameter("courseId"));
            enrollmentService.createEnrollment(EnrollmentRequest.builder()
                    .courseId(courseId)
                    .userId(userId)
                    .build());
            response.sendRedirect(domainFrontend + "/dashboard");
        }
        else if(type.equals("premium")){
            UUID userId = UUID.fromString(request.getParameter("userId"));
            BigDecimal totalPrice = new BigDecimal(request.getParameter("price"));
            int durationMonths = Integer.parseInt(request.getParameter("duration"));
            userPremiumService.createUserPremium(userId, totalPrice, durationMonths);
            response.sendRedirect(domainFrontend + "/home");
        }

        return new ResponseEntity<>("Payment successful", HttpStatus.OK);
    }

    @GetMapping("/cancel")
    public ResponseEntity<?> handlePaymentCancel(HttpServletRequest request, HttpServletResponse response)
            throws IOException{
        response.sendRedirect(domainFrontend + "/home");
        return new ResponseEntity<>("Payment cancel", HttpStatus.OK);
    }
}
