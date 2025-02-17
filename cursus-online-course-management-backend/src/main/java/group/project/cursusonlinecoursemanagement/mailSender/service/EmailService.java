package group.project.cursusonlinecoursemanagement.mailSender.service;

public interface EmailService {
    void sendVerificationEmail(String to, String fullName, String username, String token);

    void sendPasswordResetEmail(String to, String fullName, String token);
}
