package group.project.cursusonlinecoursemanagement.user.service.impl;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import group.project.cursusonlinecoursemanagement.mailSender.service.EmailService;
import group.project.cursusonlinecoursemanagement.shared.common.util.RandomStringUtil;
import group.project.cursusonlinecoursemanagement.shared.exception.handler.DuplicateFieldException;
import group.project.cursusonlinecoursemanagement.shared.exception.handler.ExpiredAccessException;
import group.project.cursusonlinecoursemanagement.shared.exception.handler.ResourceNotFoundException;
import group.project.cursusonlinecoursemanagement.shared.filter.JwtAuthenticationFilter;
import group.project.cursusonlinecoursemanagement.shared.service.JwtService;
import group.project.cursusonlinecoursemanagement.redis.RedisService;
import group.project.cursusonlinecoursemanagement.user.domain.dto.UserDto;
import group.project.cursusonlinecoursemanagement.user.domain.dto.payload.request.AccessRefreshTokenRequest;
import group.project.cursusonlinecoursemanagement.user.domain.dto.payload.request.LoginRequest;
import group.project.cursusonlinecoursemanagement.user.domain.dto.payload.request.RegisterRequest;
import group.project.cursusonlinecoursemanagement.user.domain.dto.payload.response.AuthenticationResponse;
import group.project.cursusonlinecoursemanagement.user.domain.dto.payload.response.IntrospectResponse;
import group.project.cursusonlinecoursemanagement.user.domain.entity.Role;
import group.project.cursusonlinecoursemanagement.user.domain.entity.Status;
import group.project.cursusonlinecoursemanagement.user.domain.entity.User;
import group.project.cursusonlinecoursemanagement.user.repository.UserRepository;
import group.project.cursusonlinecoursemanagement.user.service.AuthenticationService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    private final RedisService redisService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final JwtAuthenticationFilter jwtAuthenFilter;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final EmailService emailService;

    @Value("${jwt.expiration-milliseconds}")
    private long jwtExpiration;

    @Value("${jwt.refresh-expiration-time}")
    private long refreshExpirationTime;

    @Value("${jwt.expiration-verify-email}")
    private long jwtExpirationVerifyEmail;

    public AuthenticationServiceImpl(RedisService redisService, AuthenticationManager authenticationManager,
                                     JwtService jwtService, JwtAuthenticationFilter jwtAuthenFilter,
                                     UserRepository userRepository,
                                     PasswordEncoder passwordEncoder,
                                     ModelMapper modelMapper,
                                     EmailService emailService) {
        this.redisService = redisService;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.jwtAuthenFilter = jwtAuthenFilter;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
        this.emailService = emailService;
    }

    /**
     * Authenticates a user based on the provided login request.
     *
     * @param request The login request containing the user's email and password.
     * @return An AuthenticationResponse object containing the JWT token and refresh token.
     * @throws org.springframework.security.core.AuthenticationException if authentication fails.
     */
    @Override
    public AuthenticationResponse login(LoginRequest request) {

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtService.generateToken(authentication, jwtExpiration);
        String refreshToken = jwtService.generateRefreshToken(authentication, refreshExpirationTime);
        return new AuthenticationResponse(token, refreshToken);

    }

    /**
     * Handles the refresh access token process for a user.
     *
     * @param request containing the user's refresh-access token .
     * @return An AuthenticationResponse object containing the JWT token and refresh token.
     */
    @Override
    public AuthenticationResponse refreshAccessToken(AccessRefreshTokenRequest request) {
        try {
            String userEmail = jwtService.extractUsernameFromTokenWithoutVerify(request.getAccessToken());
            String storedRefreshToken = redisService.getRefreshTokenByUser(userEmail);

            if (storedRefreshToken != null && storedRefreshToken.equals(request.getRefreshToken())) {
                UserDto userDto = userRepository.findByEmailIgnoreCase(userEmail)
                        .map(UserDto::convertEntityToDto)
                        .orElseThrow(() -> new RuntimeException("Refresh token user not found"));

                String accessToken = jwtService.generateToken(UserDto.convertDtoToEntity(userDto), jwtExpiration);
                String newRefreshToken = jwtService.generateRefreshToken(UserDto.convertDtoToEntity(userDto), refreshExpirationTime);

                return new AuthenticationResponse(accessToken, newRefreshToken);
            } else {
                throw new RuntimeException("Invalid refresh token or expired");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error refreshing token: " + e.getMessage(), e);
        }
    }

    /**
     * Registers a new user based on the provided registration request.
     *
     * @param request The registration request containing the user's details such as email, phone, password, etc.
     * @return An AuthenticationResponse object containing the JWT token and refresh token.
     * @throws DuplicateFieldException if the email or phone number already exists.
     * @throws RuntimeException if the registration process fails.
     */
    @Override
    public AuthenticationResponse register(RegisterRequest request) {
        //Check duplication
        if (existsByEmailAndPhone(request.getEmail(), request.getPhone())) {
            throw new DuplicateFieldException("Email or phone number already exists");
        }
        try {
            //Create new user
            UserDto userDto = modelMapper.map(request, UserDto.class);
            userDto.setPassword(passwordEncoder.encode(request.getPassword()));
            User user = UserDto.convertDtoToEntity(userDto);
            HashSet<Role> roles = new HashSet<>();
            roles.add(Role.ROLE_STUDENT);
            user.setStatus(Status.AVAILABLE);
            user.setRoles(roles);
            user.setJoinDate(LocalDateTime.now());
            //Save user
            userRepository.save(user);
            //Create token
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String token = jwtService.generateToken(authentication, jwtExpiration);
            String refreshToken = jwtService.generateRefreshToken(authentication, refreshExpirationTime);
            //Send mail verify
            emailService.sendVerificationEmail(user.getEmail(), user.getFullName(), user.getEmail(), token);
            //Authentication login into system
            return new AuthenticationResponse(token, refreshToken);
        } catch (Exception ex) {
            throw new RuntimeException("Registration failed", ex);
        }
    }

    /**
     * Authenticates a user using their Google account based on the provided access token.
     *
     * @param accessToken The access token from Google used to authenticate the user.
     * @return An AuthenticationResponse object containing the JWT token and refresh token if a new user is created,
     *         or only the JWT token if the user already exists.
     * @throws RuntimeException if there is an error during Firebase authentication or user creation.
     */
    @Override
    public AuthenticationResponse loginWithGoogle(String accessToken) {
        try {
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(accessToken);
            UserDto userDto = userRepository.findByEmailIgnoreCase(decodedToken.getEmail()).map(UserDto::convertEntityToDto).orElse(null);
            if (userDto != null) {
                String token = jwtService.generateToken(UserDto.convertDtoToEntity(userDto), jwtExpiration);
                String refreshToken = jwtService.generateRefreshToken(UserDto.convertDtoToEntity(userDto), refreshExpirationTime);
                return new AuthenticationResponse(token, refreshToken);
            } else {
                UserDto newUserDto = new UserDto();
                newUserDto.setEmail(decodedToken.getEmail());
                newUserDto.setFullName(decodedToken.getName());
                newUserDto.setAvatarImageUrl(decodedToken.getPicture());
                HashSet<Role> roles = new HashSet<>();
                roles.add(Role.ROLE_STUDENT);
                String randomPassword = RandomStringUtil.randomAlphaNumeric(10);
                newUserDto.setPassword(passwordEncoder.encode(randomPassword));
                User newUser = UserDto.convertDtoToEntity(newUserDto);
                newUser.setRoles(roles);
                newUser.setJoinDate(LocalDateTime.now());
                newUser.setStatus(Status.AVAILABLE);
                userRepository.save(newUser);
                String token = jwtService.generateToken(newUser, jwtExpiration);
                String refreshToken = jwtService.generateRefreshToken(newUser, refreshExpirationTime);
                emailService.sendVerificationEmail(newUser.getEmail(), newUser.getFullName(), newUser.getEmail(), token);
                return new AuthenticationResponse(token, refreshToken);
            }
        } catch (FirebaseAuthException e) {
            throw new RuntimeException("Firebase Authentication Error", e);
        }
    }

    /**
     * Verifies a user's email using the provided access token.
     *
     * @param accessToken The access token used to verify the user's email.
     * @return An AuthenticationResponse object containing the new access token and refresh token.
     * @throws ResourceNotFoundException if the user is not found.
     * @throws ExpiredAccessException if both the access token and refresh token are expired.
     * @throws RuntimeException if there is an error during the email verification process.
     */
    @Override
    public AuthenticationResponse verifyEmailByToken(String accessToken) {
        String username;
        User user;
        try {
            //Check user by token
            username = jwtService.decodeJWT(accessToken).getSubject();
            //Find user in database
             user = userRepository.findByEmailIgnoreCase(username)
                    .orElseThrow(() -> new ResourceNotFoundException("User", "email", username));
            //Active the account
            user.setVerify(true);
            userRepository.save(user);
        } catch (Exception ex) {
            throw new RuntimeException("Fail verify email", ex);
        }
        //Create token login into system
        String newAccessToken;
        String storedRefreshToken;
        //Nếu access token còn tồn tại thì trả về cả 2 token nếu không thì kiểm tra RefreshToken
        if (redisService.hasToken(username)) {
            newAccessToken = redisService.getTokenByUser(username);
            storedRefreshToken = redisService.getRefreshTokenByUser(username);
        } else
            //Nếu RefreshToken còn tồn tại thì taọ ra 2 token mới
            if (redisService.hasRefreshToken(username)) {
                newAccessToken = jwtService.generateToken(user, jwtExpiration);
                storedRefreshToken = jwtService.generateRefreshToken(user, refreshExpirationTime);
            } else {
                throw new ExpiredAccessException("You need to login again because you are expired your access.");
            }
        return new AuthenticationResponse(newAccessToken, storedRefreshToken);
    }

    /**
     * Verifies a JWT token.
     *
     * @param token The JWT token to verify.
     * @return true if the token is valid, false otherwise.
     */
    @Override
    public Boolean verifyToken(String token) {
        try {
            return jwtService.validateToken(token);
        } catch (Exception ex) {
            throw new RuntimeException("Token validation failed", ex);
        }
    }


    /**
     * Initiates the password reset process for a user with the provided email.
     *
     * @param email The email of the user who wants to reset their password.
     * @return true if the password reset email was sent successfully, false otherwise.
     * @throws ResourceNotFoundException if the user with the provided email is not found.
     * @throws RuntimeException if there is an error during the password reset process.
     */
    @Override
    public Boolean forgotPassword(String email) {
        try {
            User user = getUserByEmail(email);
            String token = jwtService.generateToken(user, jwtExpirationVerifyEmail);
            emailService.sendPasswordResetEmail(user.getEmail(), user.getFullName(), token);
            return true;
        } catch (ResourceNotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RuntimeException("Password reset process failed", ex);
        }
    }

    @Override
    public IntrospectResponse introspectToken(String token) {
        try {
            // Remove the "Bearer " prefix from the token
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            // Decode the token
            String username = jwtService.extractUsernameFromToken(token);
            List<String> roles = jwtService.extractRolesFromToken(token);

            // Create and return the IntrospectResponse
            return new IntrospectResponse(username, roles);
        } catch (Exception ex) {
            throw new RuntimeException("Token introspection failed", ex);
        }
    }

    @Override
    public void changePassword(String email, String newPassword) {
        try {
            User user = getUserByEmail(email);
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
        } catch (ResourceNotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RuntimeException("Password change process failed", ex);
        }

    }


    /**
     * Verifies a user's email.
     *
     * @param email The email of the user to verify.
     * @return true if the email is valid and associated with a user, false otherwise.
     */
    @Override
    public Boolean sendVerifyEmail(String email) {
        try {
            User user = getUserByEmail(email);
            String token = jwtService.generateToken(user, jwtExpirationVerifyEmail);
            emailService.sendVerificationEmail(user.getEmail(), user.getFullName(), user.getEmail(), token);
            return true;
        } catch (ResourceNotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RuntimeException("Email verification process failed", ex);
        }
    }

    /**
     * Checks if a user exists by their email and phone number.
     *
     * @param email The email of the user.
     * @param phone The phone number of the user.
     * @return true if the user exists, false otherwise.
     */
    private Boolean existsByEmailAndPhone(String email, String phone) {
        return userRepository.existsByEmailIgnoreCase(email) || userRepository.existsByPhone(phone);
    }

    /**
     * Retrieves a User object by their email.
     *
     * @param email The email of the user.
     * @return A User object containing the user's details.
     * @throws ResourceNotFoundException if the user is not found.
     */
    private User getUserByEmail(String email) {
        try {
            return userRepository.findByEmailIgnoreCase(email)
                    .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        } catch (ResourceNotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RuntimeException("Failed to retrieve user by email", ex);
        }
    }
}
