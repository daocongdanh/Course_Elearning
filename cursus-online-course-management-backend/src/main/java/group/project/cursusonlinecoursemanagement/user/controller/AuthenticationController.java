package group.project.cursusonlinecoursemanagement.user.controller;

import group.project.cursusonlinecoursemanagement.shared.service.JwtService;
import group.project.cursusonlinecoursemanagement.user.domain.dto.payload.request.AccessRefreshTokenRequest;
import group.project.cursusonlinecoursemanagement.user.domain.dto.payload.request.LoginRequest;
import group.project.cursusonlinecoursemanagement.user.domain.dto.payload.request.RegisterRequest;
import group.project.cursusonlinecoursemanagement.user.domain.dto.payload.request.ResetPasswordRequest;
import group.project.cursusonlinecoursemanagement.user.domain.dto.payload.response.AuthenticationResponse;
import group.project.cursusonlinecoursemanagement.user.domain.dto.payload.response.IntrospectResponse;
import group.project.cursusonlinecoursemanagement.user.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(
        name = "Authentication and Authorization"
)

@Controller
@RequestMapping("/api")
public class AuthenticationController {
    
    private final AuthenticationService authenticationService;
    private final JwtService jwtService;
    
    public AuthenticationController(AuthenticationService authenticationService, JwtService jwtService) {
        this.authenticationService = authenticationService;
        this.jwtService = jwtService;
    }

    @PostMapping("/auth/register")
    @Operation(summary = "User Registration", description = "Registers a new user with the provided registration request.")
    @ApiResponse(responseCode = "200", description = "Http Status 200 SUCCESS")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody @Valid RegisterRequest request) {
        AuthenticationResponse authResponse = authenticationService.register(request);
        return ResponseEntity.ok(authResponse);
    }
    
    @PostMapping("/auth/login")
    @Operation(summary = "User Login", description = "Authenticates a user with the provided username and password.")
    @ApiResponse(responseCode = "200", description = "Http Status 200 SUCCESS")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody LoginRequest request) {
        AuthenticationResponse authResponse = authenticationService.login(request);
        return new ResponseEntity<>(authResponse, HttpStatus.OK);
    }
    
    @PostMapping("/auth/loginWithGoogle")
    @Operation(summary = "User Login with Google", description = "Authenticates a user with a Google access token.")
    @ApiResponse(responseCode = "200", description = "Http Status 200 SUCCESS")
    public ResponseEntity<AuthenticationResponse> loginWithGoogle(@RequestBody Map<String, String> payload) {
        AuthenticationResponse authResponse = authenticationService.loginWithGoogle(payload.get("access_token"));
        return new ResponseEntity<>(authResponse, HttpStatus.OK);
    }
    
    @PostMapping("/auth/refreshToken")
    @Operation(summary = "Refresh Token", description = "Refresh a user's token.")
    @ApiResponse(responseCode = "200", description = "Http Status 200 SUCCESS")
    public ResponseEntity<AuthenticationResponse> refreshToken(@RequestBody AccessRefreshTokenRequest request) {
            AuthenticationResponse authResponse = authenticationService.refreshAccessToken(request);
            return new ResponseEntity<>(authResponse, HttpStatus.OK);
    }
    
    @GetMapping("/verifyEmailByToken/{token}")
    @Operation(summary = "Verify Email by Token", description = "Verifies a user's email using the provided token.")
    @ApiResponse(responseCode = "200", description = "Http Status 200 SUCCESS")
    @ApiResponse(responseCode = "400", description = "Http Status 400 BAD REQUEST")
    @ApiResponse(responseCode = "404", description = "Http Status 404 NOT FOUND")
    public ResponseEntity<AuthenticationResponse> verifyEmailByToken(@PathVariable String token) {
        AuthenticationResponse authResponse = authenticationService.verifyEmailByToken(token);
        return new ResponseEntity<>(authResponse, HttpStatus.OK);
    }
    
    @GetMapping("/auth/verifyTokenForResetPassword/{token}")
    @Operation(summary = "Verify Token For Reset Password", description = "Verifies a user's change password token.")
    public String verifyTokenForResetPassword(@PathVariable("token") String token, Model model) {
        boolean isVerified = authenticationService.verifyToken(token);
        
        if (isVerified) {
            model.addAttribute("token", token);
            return "resetPassword";
        }
        return "paymentError";
    }
    
    @PostMapping("/auth/resetPassword")
    @Operation(summary = "Reset password", description = "Change a user's password.")
    public String resetPassword(@RequestBody @Valid ResetPasswordRequest resetPasswordRequest) {
        System.out.println(resetPasswordRequest);
        System.out.println("Token: " + resetPasswordRequest.getVerificationToken());
        boolean isVerified = authenticationService.verifyToken(resetPasswordRequest.getVerificationToken());
        if (isVerified) {
            String email = jwtService.extractUsernameFromToken(resetPasswordRequest.getVerificationToken());
            authenticationService.changePassword(email, resetPasswordRequest.getPassword());
            return "paymentSuccess";
        }
        
        return "paymentError";
    }
    
    @GetMapping("/auth/sendVerifyEmail/{email}")
    @Operation(summary = "Verify Email", description = "Verifies a user's email.")
    @ApiResponse(responseCode = "200", description = "Http Status 200 SUCCESS")
    public ResponseEntity<Boolean> verifyEmail(@PathVariable String email) {
        boolean isVerified = authenticationService.sendVerifyEmail(email);
        return ResponseEntity.ok(isVerified);
    }
    
    @GetMapping("/auth/forgotPassword/{email}")
    @Operation(summary = "Forgot Password", description = "Initiates a password recovery operation for a user with the provided email.")
    @ApiResponse(responseCode = "200", description = "Http Status 200 SUCCESS")
    public ResponseEntity<Boolean> forgotPassword(@PathVariable String email) {
        boolean isSuccessful = authenticationService.forgotPassword(email);
        return ResponseEntity.ok(isSuccessful);
    }
    
    @GetMapping("/auth/introspect")
    @Operation(summary = "Introspect Token", description = "Introspects a user's token.")
    @ApiResponse(responseCode = "200", description = "Http Status 200 SUCCESS")
    @SecurityRequirement(
            name = "Bear Authentication"
    )
    public ResponseEntity<IntrospectResponse> introspect(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        IntrospectResponse introspectResponse = authenticationService.introspectToken(token);
        return ResponseEntity.ok(introspectResponse);
    }

    @GetMapping("/auth/verifyToken")
    @Operation(summary = "Verify Token", description = "Verifies a user's token.")
    @ApiResponse(responseCode = "200", description = "Http Status 200 SUCCESS")
    public ResponseEntity<String> verifyToken(@RequestParam String token) {
        System.out.println("Verify token: " + token);
        try {
            authenticationService.verifyToken(token);
            return ResponseEntity.ok("Token is valid");
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Invalid token");
        }
    }
    
}