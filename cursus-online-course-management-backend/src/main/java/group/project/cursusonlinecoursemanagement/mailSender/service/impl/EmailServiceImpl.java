package group.project.cursusonlinecoursemanagement.mailSender.service.impl;

import group.project.cursusonlinecoursemanagement.mailSender.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;

@Service
public class EmailServiceImpl implements EmailService {

    public static final String UTF_8_ENCODING = "UTF-8";
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${domain.frontend}")
    private String domainFrontend;

    public EmailServiceImpl(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    /**
     * Sends a verification email to the user after registration.
     * The email contains a link for the user to verify their account.
     *
     * @param to       The recipient's email address.
     * @param fullName The recipient's full name.
     * @param username The recipient's username.
     * @param token    The verification token to be included in the email.
     * @throws RuntimeException if an error occurs while sending the email.
     */
    @Override
    @Async("taskExecutor")
    public void sendVerificationEmail(String to, String fullName, String username, String token) {
        try {
            Context context = new Context();
            Map<String, String> variables = Map.of("fullName", fullName, "username", username, "url",domainFrontend+ "/authenticate/verify/" + token);
            variables.forEach(context::setVariable);
            String text = templateEngine.process("verifications", context);
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, UTF_8_ENCODING);

            helper.setPriority(1);
            helper.setSubject("Account Verification");
            helper.setFrom(fromEmail);
            helper.setTo(to);

            helper.setText(text, true);
            mailSender.send(message);
        } catch (MessagingException exception) {
            if (exception.getMessage().contains("Recipient address rejected")) {
                throw new RuntimeException("Recipient address rejected: The email address does not exist.");
            } else {
                throw new RuntimeException("Failed to send verification email: " + exception.getMessage());
            }
        } catch (Exception exception) {
            throw new RuntimeException(exception.getMessage());
        }
    }

    /**
     * Sends a password reset email to the user.
     * The email contains a link for the user to reset their password.
     *
     * @param to       The recipient's email address.
     * @param fullName The recipient's full name.
     * @param token    The password reset token to be included in the email.
     * @throws RuntimeException if an error occurs while sending the email.
     */
    @Override
    @Async("taskExecutor")
    public void sendPasswordResetEmail(String to, String fullName, String token) {
        try {
            Context context = new Context();
            Map<String, String> variables = Map.of("fullName", fullName, "url", domainFrontend + "/authenticate/forgot-password/" + token);
            variables.forEach(context::setVariable);
            String text = templateEngine.process("resetPasswordEmail", context);
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, UTF_8_ENCODING);
            helper.setPriority(1);
            helper.setSubject("Password Reset");
            helper.setFrom(fromEmail);
            helper.setTo(to);

            helper.setText(text, true);
            mailSender.send(message);
        } catch (Exception exception) {
            throw new RuntimeException(exception.getMessage());
        }
    }

}
