package group.project.cursusonlinecoursemanagement.mailSender.controller;

import group.project.cursusonlinecoursemanagement.mailSender.service.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(
        name = "CRUD REST APIs for Email"
)
@RestController
@RequestMapping("/api/email")
public class SendMailController {
    
    private final EmailService emailService;
    
    public SendMailController(EmailService emailService) {
        this.emailService = emailService;
    }
    
    @Operation(
            summary = "Send Verification Email REST API",
            description = "Send Verification Email REST API is used to send a verification email to the user after registration."
    )
    @PostMapping("/sendVerificationEmail")
    public ResponseEntity<?> sendVerificationEmail(@RequestParam String to,
                                                   @RequestParam String fullName,
                                                   @RequestParam String username,
                                                   @RequestParam String token)
    {
        emailService.sendVerificationEmail(to, fullName, username, token);
        return ResponseEntity.ok("Email sent successfully");
    }

    @Operation(
            summary = "Send Password Reset Email REST API",
            description = "Send Password Reset Email REST API is used to send a password reset email to the user."
    )
    @PostMapping("/sendPasswordResetEmail")
    public ResponseEntity<?> sendPasswordResetEmail(@RequestParam String to,
                                                    @RequestParam String fullName,
                                                    @RequestParam String token)
    {
        emailService.sendPasswordResetEmail(to, fullName, token);
        return ResponseEntity.ok("Email sent successfully");
    }
}
