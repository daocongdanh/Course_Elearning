package group.project.cursusonlinecoursemanagement.shared.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import group.project.cursusonlinecoursemanagement.redis.RedisService;
import group.project.cursusonlinecoursemanagement.user.domain.entity.Role;
import group.project.cursusonlinecoursemanagement.user.domain.entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class JwtService {

    private final RedisService redisService;

    @Value("${jwt.secret}")
    private String jwtSecret;

    public JwtService(RedisService redisService) {
        this.redisService = redisService;
    }
    
    /**
     * Generates a JWT token for the authenticated user.
     *
     * @param authentication The Authentication object containing the authenticated user's details.
     * @param jwtExpiration  The duration (in milliseconds) for which the JWT token is valid.
     * @return A JWT token as a String.
     */
    public String generateToken(Authentication authentication, long jwtExpiration) {
        Date expirationDate = new Date(System.currentTimeMillis() + jwtExpiration);
        
        // Create a string from the list of user roles
        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        String scope = String.join(" ", roles);
        String token = Jwts.builder()
                .subject(authentication.getName())
                .issuedAt(new Date())
                .expiration(expirationDate)
                .claim("scope", scope) // Add scope to JWTClaimsSet
                .signWith(key())
                .compact();
        redisService.saveToken(authentication.getName(), token, jwtExpiration);
        return token;
    }
    
    /**
     * Generates a JWT refresh token for the authenticated user.
     *
     * @param authentication The Authentication object containing the authenticated user's details.
     * @param refreshExpirationTime  The duration (in milliseconds) for which the JWT refresh token is valid.
     * @return A JWT refresh token as a String.
     */
    public String generateRefreshToken(Authentication authentication, long refreshExpirationTime) {
        try {
            String key =UUID.randomUUID().toString().replace("-", "");
            redisService.saveRefreshToken(authentication.getName(),key,refreshExpirationTime);
            return key;
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate refresh token", e);
        }
    }
    /**
     * Generates a JWT token for the specified user.
     *
     * @param user          The User object for which the JWT token is to be generated.
     * @param jwtExpiration The duration (in milliseconds) for which the JWT token is valid.
     * @return A JWT token as a String.
     */
    public String generateToken(User user, long jwtExpiration) {
        JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.HS256);

        List<String> roles = user.getRoles().stream().map(Role::name).collect(Collectors.toList());
        String scope = String.join(" ", roles);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getEmail())
                .issueTime(new Date())
                .expirationTime(new Date(Instant.now().plus(jwtExpiration, ChronoUnit.MILLIS).toEpochMilli()))
                .claim("scope", scope)
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        JWSObject jwsObject;

        try {
            jwsObject = new JWSObject(jwsHeader, payload);
            jwsObject.sign(new MACSigner(key()));
            String token = jwsObject.serialize();
            redisService.saveToken(user.getEmail(),token,jwtExpiration);
            return token;
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * Generates a JWT refresh token for the authenticated user.
     *
     * @param user The user entity containing user's details.
     * @param refreshExpirationTime  The duration (in milliseconds) for which the JWT refresh token is valid.
     * @return A JWT refresh token as a String.
     */
    public String generateRefreshToken(User user, long refreshExpirationTime) {
        try {
            String key =UUID.randomUUID().toString().replace("-", "");
            redisService.saveRefreshToken(user.getEmail(),key,refreshExpirationTime);
            return key;
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate refresh token", e);
        }
    }

    /**
     * Generates a SecretKey for signing the JWT token.
     *
     * @return A SecretKey object.
     */
    private SecretKey key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    /**
     * Extracts the username from the specified JWT token.
     *
     * @param token The JWT token from which the username is to be extracted.
     * @return The username as a String.
     */
    public String extractUsernameFromToken(String token) {
        return Jwts.parser()
                .verifyWith(key())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }
    public String extractUsernameFromTokenWithoutVerify(String token) {
        String[] parts = token.split("\\.");
        if (parts.length < 2) {
            throw new RuntimeException("Invalid access token");
        }
        String payload = new String(Base64.getUrlDecoder().decode(parts[1]));
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(payload);
            return jsonNode.get("sub").asText();
        } catch (Exception e) {
            throw new RuntimeException("Error get user from token");
        }
    }

    public Claims decodeJWT(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(key())
//                    .setSigningKey(key())
                    .build()
                    .parseSignedClaims(token)
//                    .parseClaimsJws(token)
                    .getPayload();
//                    .getBody();
        } catch (ExpiredJwtException e) {
            // Lấy Claims ngay cả khi token đã hết hạn
            return e.getClaims();
        } catch (MalformedJwtException | IllegalArgumentException malformedJwtException) {
            throw new BadCredentialsException("Invalid token");
        } catch (UnsupportedJwtException unsupportedJwtException) {
            throw new BadCredentialsException("Unsupported token");
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse JWT");
        }
    }


    public List<String> extractRolesFromToken(String token) {
        String scope = Jwts.parser()
                .verifyWith(key())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("scope", String.class);

        // Split the scope string by spaces and collect as a list
        return Arrays.stream(scope.split(" "))
                .collect(Collectors.toList());
    }

    /**
     * Validates the specified JWT token.
     *
     * @param token The JWT token to be validated.
     * @return true if the token is valid, false otherwise.
     * @throws BadCredentialsException if the token is invalid, expired, or unsupported.
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(key())
                    .build()
                    .parse(token);
            String email = extractUsernameFromToken(token);
            if(redisService.hasToken(email)&&redisService.getTokenByUser(email).equals(token)){
                return true;
            }else {
                throw new BadCredentialsException("Expired token");
            }
        } catch (MalformedJwtException | IllegalArgumentException malformedJwtException) {
            throw new BadCredentialsException("Invalid token");
        } catch (ExpiredJwtException expiredJwtException) {
            throw new BadCredentialsException("Expired token");
        } catch (UnsupportedJwtException unsupportedJwtException) {
            throw new BadCredentialsException("Unsupported token");
        }
    }


}
