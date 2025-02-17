package group.project.cursusonlinecoursemanagement.payment.controller;

import group.project.cursusonlinecoursemanagement.payment.domain.dto.request.CreatePaymentRequest;
import group.project.cursusonlinecoursemanagement.payment.service.PaymentRequestService;
import group.project.cursusonlinecoursemanagement.user.domain.dto.payload.response.InstructorResponse;
import group.project.cursusonlinecoursemanagement.user.domain.entity.User;
import group.project.cursusonlinecoursemanagement.user.service.InstructorService;
import group.project.cursusonlinecoursemanagement.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(
        name = "CRUD REST APIs for Payment Request"
)
@RestController
@RequestMapping("/api/paymentRequest")
public class PaymentRequestController {
    private final PaymentRequestService paymentRequestService;

    public PaymentRequestController(PaymentRequestService paymentRequestService) {
        this.paymentRequestService = paymentRequestService;
    }

    @Operation(
            summary = "Create Payment Request REST API",
            description = "Create Payment Request REST API is used to save payment request into database"
    )
    @ApiResponse(
            responseCode = "201",
            description = "Http Status 201 CREATED"
    )
    @SecurityRequirement(
            name = "Bear Authentication"
    )
    @PreAuthorize("hasRole('ROLE_INSTRUCTOR')")
    @PostMapping("/auth/createPaymentRequest")
    public ResponseEntity<?> createPaymentRequest(@RequestBody CreatePaymentRequest createPaymentRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return new ResponseEntity<>(paymentRequestService.createPaymentRequest(email, createPaymentRequest), HttpStatus.CREATED);
    }

    @SecurityRequirement(
            name = "Bear Authentication"
    )
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/getPaymentRequestIsNotDoneById/{id}")
    public ResponseEntity<?> getPaymentRequestIsNotDoneById(@PathVariable UUID id) {
        return new ResponseEntity<>(paymentRequestService.getPaymentRequestById(id), HttpStatus.OK);
    }

    @Operation(
            summary = "Get isDone Payment Request REST API",
            description = "Get isDone Payment Request REST API"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Http Status 200"
    )
    @SecurityRequirement(
            name = "Bear Authentication"
    )
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/getAllIsNotDonePaymentRequest")
    public ResponseEntity<?> getPaymentRequestIsNotDone() {
        return new ResponseEntity<>(paymentRequestService.getAllPaymentRequestIsNotDone(), HttpStatus.OK);
    }

    @Operation(
            summary = "Get isNotDone Payment Request REST API",
            description = "Get isNotDone Payment Request REST API"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Http Status 200"
    )
    @SecurityRequirement(
            name = "Bear Authentication"
    )
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/getAllIsDonePaymentRequest")
    public ResponseEntity<?> getPaymentRequestIsDone() {
        return new ResponseEntity<>(paymentRequestService.getAllPaymentRequestIsDone(), HttpStatus.OK);
    }

    @Operation(
            summary = "Change status of payment request",
            description = "Change status 'isDone' of payment request from false to true"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Http Status 200"
    )
    @SecurityRequirement(
            name = "Bear Authentication"
    )
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/changeStatusForRequest/{id}")
    public ResponseEntity<?> changeStatusForRequest(@PathVariable UUID id) {
        return new ResponseEntity<>(paymentRequestService.isDone(id), HttpStatus.OK);
    }

}
