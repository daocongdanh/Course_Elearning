package group.project.cursusonlinecoursemanagement.payment.controller;

import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import group.project.cursusonlinecoursemanagement.payment.domain.dto.response.GetPaymentRequestResponse;
import group.project.cursusonlinecoursemanagement.payment.service.PaymentRequestService;
import group.project.cursusonlinecoursemanagement.payment.service.PaymentService;
import group.project.cursusonlinecoursemanagement.shared.exception.handler.AppExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.UUID;

@Controller
@RequestMapping("/api/paypal")
@Slf4j
public class PaypalController {

    private final PaymentService paypalService;
    private final PaymentRequestService paymentRequestService;

    public PaypalController(PaymentService paypalService, PaymentRequestService paymentRequestService) {
        this.paypalService = paypalService;
        this.paymentRequestService = paymentRequestService;
    }

    @GetMapping("/createFormForUser/{amount}")
    public String createFormForUser(
            @PathVariable Double amount,
            Model model
    ) {
        model.addAttribute("amount", amount);
        model.addAttribute("description", "Transfer money to buy a course");
        model.addAttribute("payeeId", "sb-w3nfo30755669@gmail.com");
        return "paymentCreate";
    }

    @GetMapping("/createFormForAdmin/{paymentRequestId}")
    public String createFormForAdmin(
            @PathVariable UUID paymentRequestId,
            Model model
    ) {
        GetPaymentRequestResponse getPaymentRequestResponse = paymentRequestService.getPaymentRequestById(paymentRequestId);
        model.addAttribute("amount", getPaymentRequestResponse.getAmount());
        model.addAttribute("description", "Transfer money for instructor");
        model.addAttribute("payeeId", getPaymentRequestResponse.getInstructorPaypalId());
        return "paymentCreate";
    }

    @PostMapping("/create")
    public RedirectView createPayment(
            @RequestParam("method") String method,
            @RequestParam("amount") String amount,
            @RequestParam("currency") String currency,
            @RequestParam("description") String description,
            @RequestParam("payeeId") String payeeId
    ) {
        try {
            String cancelUrl = "http://localhost:8080/api/payment/cancel";
            String successUrl = "http://localhost:8080/api/paypal/cancel";
            Payment payment = paypalService.createPayment(
                    Double.valueOf(amount),
                    currency,
                    method,
                    "sale",
                    description,
                    cancelUrl,
                    successUrl,
                    payeeId
            );

            for (Links links : payment.getLinks()) {
                if (links.getRel().equals("approval_url")) {
                    return new RedirectView(links.getHref());
                }
            }
        } catch (PayPalRESTException e) {
            throw new AppExceptionHandler("Error: " + e);
        }
        return new RedirectView("/api/payment/error");
    }

    @GetMapping("/success")
    public ResponseEntity<?> paymentSuccess(
            @RequestParam("paymentId") String paymentId,
            @RequestParam("PayerID") String payerId
    ) {
        try {
            Payment payment = paypalService.executePayment(paymentId, payerId);
            if (payment.getState().equals("approved")) {
                  return new ResponseEntity<>("Payment success", HttpStatus.OK);
//                return "paymentSuccess";
            }
        } catch (PayPalRESTException e) {
            throw new AppExceptionHandler("Error: " + e);
        }
        return new ResponseEntity<>("Payment success", HttpStatus.OK);
    }

    @GetMapping("/cancel")
    public ResponseEntity<?> paymentCancel() {

        return new ResponseEntity<>("Payment canceled", HttpStatus.OK);
//        return "paymentCancel";
    }

    @GetMapping("/error")
    public ResponseEntity<?> paymentError() {

        return new ResponseEntity<>("Payment error", HttpStatus.BAD_REQUEST);
//        return "paymentError";
    }
}
