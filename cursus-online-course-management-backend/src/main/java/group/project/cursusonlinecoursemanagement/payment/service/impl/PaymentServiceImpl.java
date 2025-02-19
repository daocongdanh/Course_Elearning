package group.project.cursusonlinecoursemanagement.payment.service.impl;

import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import group.project.cursusonlinecoursemanagement.course.domain.dto.payload.response.EnrollmentResponse;
import group.project.cursusonlinecoursemanagement.course.service.EnrollmentService;
import group.project.cursusonlinecoursemanagement.payment.domain.dto.request.CreateEnrollRequestPayment;
import group.project.cursusonlinecoursemanagement.payment.domain.dto.request.EnrollRequestPayment;
import group.project.cursusonlinecoursemanagement.payment.domain.dto.response.GetPaymentRequestResponse;
import group.project.cursusonlinecoursemanagement.payment.service.PaymentRequestService;
import group.project.cursusonlinecoursemanagement.payment.service.PaymentService;
import group.project.cursusonlinecoursemanagement.premium.domain.dto.request.CreatePaymentPremium;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import vn.payos.PayOS;
import vn.payos.type.CheckoutResponseData;
import vn.payos.type.ItemData;
import vn.payos.type.PaymentData;

import java.util.*;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final APIContext apiContext;
    private final EnrollmentService enrollmentService;
    private final PaymentRequestService paymentRequestService;
    private final PayOS payOS;

    @Value("${payos.return-url}")
    private String returnUrl;
    @Value("${payos.cancel-url}")
    private String cancelUrl;

    public PaymentServiceImpl(APIContext apiContext, EnrollmentService enrollmentService,
                              PaymentRequestService paymentRequestService, PayOS payOS) {
        this.apiContext = apiContext;
        this.enrollmentService = enrollmentService;
        this.paymentRequestService = paymentRequestService;
        this.payOS = payOS;
    }

    public Payment createPayment(
            Double total,
            String currency,
            String method,
            String intent,
            String description,
            String cancelUrl,
            String successUrl,
            String payeeId
    ) throws PayPalRESTException {
        Amount amount = new Amount();
        amount.setCurrency(currency);
        amount.setTotal(String.format(Locale.forLanguageTag(currency), "%.2f", total)); // 9.99$ - 9,99€

        Transaction transaction = new Transaction();
        transaction.setDescription(description);
        transaction.setAmount(amount);
        Payee payee = new Payee();
        payee.setEmail(payeeId);
        transaction.setPayee(payee);
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);

        Payer payer = new Payer();
        payer.setPaymentMethod(method);

        Payment payment = new Payment();
        payment.setIntent(intent);
        payment.setPayer(payer);
        payment.setTransactions(transactions);

        RedirectUrls redirectUrls = new RedirectUrls();
        redirectUrls.setCancelUrl(cancelUrl);
        redirectUrls.setReturnUrl(successUrl);

        payment.setRedirectUrls(redirectUrls);

        return payment.create(apiContext);
    }

    @Override
    public Payment createPaymentWithPaymentRequest(UUID paymentRequestId, String returnUrl, String cancelUrl) throws PayPalRESTException{
        GetPaymentRequestResponse getPaymentRequestResponse = paymentRequestService.getPaymentRequestById(paymentRequestId);
        Amount amount = new Amount();
        amount.setCurrency("USD");
        amount.setTotal(String.format(Locale.forLanguageTag("USD"), "%.2f", getPaymentRequestResponse.getAmount()));

        Transaction transaction = new Transaction();
        transaction.setDescription("Admin refund money");
        transaction.setAmount(amount);
        Payee payee = new Payee();
        payee.setEmail(getPaymentRequestResponse.getInstructorPaypalId());
        transaction.setPayee(payee);
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);

        Payer payer = new Payer();
        payer.setPaymentMethod("Paypal");

        Payment payment = new Payment();
        payment.setIntent("sale");
        payment.setPayer(payer);
        payment.setTransactions(transactions);

        RedirectUrls redirectUrls = new RedirectUrls();
        redirectUrls.setCancelUrl(cancelUrl);
        redirectUrls.setReturnUrl(returnUrl);

        payment.setRedirectUrls(redirectUrls);
        return payment.create(apiContext);
    }
    public Payment executePayment(
            String paymentId,
            String payerId
    ) throws PayPalRESTException {
        Payment payment = new Payment();
        payment.setId(paymentId);

        PaymentExecution paymentExecution = new PaymentExecution();
        paymentExecution.setPayerId(payerId);

        return payment.execute(apiContext, paymentExecution);
    }

    @Override
    public Payment createEnrollRequestPayment(EnrollRequestPayment enrollRequestPayment) throws PayPalRESTException {
        Amount amount = createAmount(enrollRequestPayment);
        Transaction transaction = createTransaction(enrollRequestPayment, amount);
        Payer payer = createPayer(enrollRequestPayment);
        RedirectUrls redirectUrls = createRedirectUrls(enrollRequestPayment);

        Payment payment = new Payment();
        payment.setIntent("sale");
        payment.setPayer(payer);
        payment.setTransactions(Collections.singletonList(transaction));
        payment.setRedirectUrls(redirectUrls);

        return payment.create(apiContext);
    }

    private Amount createAmount(EnrollRequestPayment enrollRequestPayment) {
        return new Amount(
                enrollRequestPayment.getCurrency(),
                String.format(Locale.forLanguageTag(enrollRequestPayment.getCurrency()), "%.2f", enrollRequestPayment.getAmount())
        );
    }

    private Transaction createTransaction(EnrollRequestPayment enrollRequestPayment, Amount amount) {
        Transaction transaction = new Transaction();
        transaction.setDescription(enrollRequestPayment.getDescription());
        transaction.setAmount(amount);

        Payee payee = new Payee();
        payee.setEmail(enrollRequestPayment.getPayeeId());
        transaction.setPayee(payee);

        return transaction;
    }

    private Payer createPayer(EnrollRequestPayment enrollRequestPayment) {
        Payer payer = new Payer();
        payer.setPaymentMethod(enrollRequestPayment.getMethod());
        return payer;
    }

    private RedirectUrls createRedirectUrls(EnrollRequestPayment enrollRequestPayment) {
        RedirectUrls redirectUrls = new RedirectUrls();
        redirectUrls.setCancelUrl(enrollRequestPayment.getCancelUrl());
        redirectUrls.setReturnUrl(enrollRequestPayment.getReturnUrl());
        return redirectUrls;
    }

    @Override
    public EnrollmentResponse executePaymentRequest(
            String paymentId,
            String payerId,
            String email,
            UUID courseId
    ) throws PayPalRESTException {
        Payment payment = new Payment();
        payment.setId(paymentId);

        PaymentExecution paymentExecution = new PaymentExecution();
        paymentExecution.setPayerId(payerId);

        Payment executedPayment = payment.execute(apiContext, paymentExecution);
        Double total = executedPayment.getTransactions().stream()
                .map(transaction -> transaction.getAmount().getTotal())
                .map(Double::parseDouble)
                .reduce(0.0, Double::sum);
        return enrollmentService.createUserEnrollment(email, courseId, total);
    }

    @Override
    public GetPaymentRequestResponse executeRefundPayment(
            String paymentId,
            String payerId,
            UUID paymentRequestId
    ) throws PayPalRESTException {
        Payment payment = new Payment();
        payment.setId(paymentId);

        PaymentExecution paymentExecution = new PaymentExecution();
        paymentExecution.setPayerId(payerId);

        Payment executedPayment = payment.execute(apiContext, paymentExecution);

        return paymentRequestService.isDone(paymentRequestId);
    }

    @Override
    public String createEnrollRequestPayment(CreateEnrollRequestPayment createEnrollRequestPayment) {
        try{
            String currentTimeString = String.valueOf(new Date().getTime());
            long orderCode = Long.parseLong(currentTimeString.substring(currentTimeString.length() - 6));
            ItemData item = ItemData.builder()
                    .name(createEnrollRequestPayment.getTitle())
                    .quantity(1)
                    .price(createEnrollRequestPayment.getAmount().intValue())
                    .build();
            PaymentData paymentData = PaymentData.builder()
                    .orderCode(orderCode)
                    .amount(createEnrollRequestPayment.getAmount().intValue())
                    .description(createEnrollRequestPayment.getDescription())
                    .returnUrl(returnUrl + "?userId=" + createEnrollRequestPayment.getUserId()
                            + "&courseId=" + createEnrollRequestPayment.getCourseId()
                            + "&type=enrollment")
                    .cancelUrl(cancelUrl)
                    .item(item).build();

            CheckoutResponseData data = payOS.createPaymentLink(paymentData);
            return data.getCheckoutUrl();
        }
        catch (Exception e){
            e.printStackTrace();
            System.out.println("------------Lỗi payment----------");
            return null;
        }
    }

    @Override
    public String createPremiumPayment(CreatePaymentPremium createPaymentPremium) {
        try{
            String currentTimeString = String.valueOf(new Date().getTime());
            long orderCode = Long.parseLong(currentTimeString.substring(currentTimeString.length() - 6));
            ItemData item = ItemData.builder()
                    .name("Premium Package")
                    .quantity(1)
                    .price(createPaymentPremium.getTotalPrice().intValue())
                    .build();

            PaymentData paymentData = PaymentData.builder()
                    .orderCode(orderCode)
                    .amount(createPaymentPremium.getTotalPrice().intValue())
                    .description("Premium Package")
                    .returnUrl(returnUrl + "?userId=" + createPaymentPremium.getUserId()
                            + "&price=" + createPaymentPremium.getTotalPrice()
                            + "&duration=" + createPaymentPremium.getDurationMonths()
                            + "&type=premium")
                    .cancelUrl(cancelUrl)
                    .item(item).build();
            CheckoutResponseData data = payOS.createPaymentLink(paymentData);
            return data.getCheckoutUrl();
        }
        catch (Exception e){
            e.printStackTrace();
            System.out.println("------------Lỗi payment----------");
            return null;
        }
    }
}
