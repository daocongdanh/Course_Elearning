package group.project.cursusonlinecoursemanagement.user.service;

import group.project.cursusonlinecoursemanagement.user.domain.dto.payload.request.AccessRefreshTokenRequest;
import group.project.cursusonlinecoursemanagement.user.domain.dto.payload.request.LoginRequest;
import group.project.cursusonlinecoursemanagement.user.domain.dto.payload.request.RegisterRequest;
import group.project.cursusonlinecoursemanagement.user.domain.dto.payload.response.AuthenticationResponse;
import group.project.cursusonlinecoursemanagement.user.domain.dto.payload.response.IntrospectResponse;

public interface AuthenticationService {

    AuthenticationResponse register(RegisterRequest userDto);

    AuthenticationResponse login(LoginRequest request);

    AuthenticationResponse loginWithGoogle(String accessToken);

    AuthenticationResponse verifyEmailByToken(String accessToken);

    Boolean verifyToken(String token);

    Boolean forgotPassword(String token);
    
    Boolean sendVerifyEmail(String email);

    AuthenticationResponse refreshAccessToken(AccessRefreshTokenRequest request);

    IntrospectResponse introspectToken(String token);
    
    void changePassword(String email, String newPassword);
}
