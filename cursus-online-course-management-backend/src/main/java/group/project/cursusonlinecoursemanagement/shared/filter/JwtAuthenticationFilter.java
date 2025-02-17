package group.project.cursusonlinecoursemanagement.shared.filter;

import group.project.cursusonlinecoursemanagement.shared.common.constraints.Endpoints;
import group.project.cursusonlinecoursemanagement.shared.service.JwtService;
import group.project.cursusonlinecoursemanagement.user.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService tokenProvider;
    private final UserService userDetailsService;

    public JwtAuthenticationFilter(JwtService tokenProvider, UserService userDetailsService) {
        this.tokenProvider = tokenProvider;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String requestURI = request.getRequestURI();

        // Skip authentication for public endpoints
        if (isPublicEndpoint(requestURI)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
        //get JWT token from HttpRequest
        String tokenFromRequest = getTokenFromRequest(request);
        if (StringUtils.hasText(tokenFromRequest) && tokenProvider.validateToken(tokenFromRequest)) {
            String username = tokenProvider.extractUsernameFromToken(tokenFromRequest);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if(userDetails != null) {
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );
                authenticationToken.setDetails(new WebAuthenticationDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }
        } catch (Exception ex) {
            throw new RuntimeException("Failed on set user authentication" + ex);
        }
        filterChain.doFilter(request, response);
    }

    private boolean isPublicEndpoint(String requestURI) {
        return Arrays.stream(Endpoints.PRIVATE_ENDPOINT)
                .noneMatch(requestURI::matches);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
