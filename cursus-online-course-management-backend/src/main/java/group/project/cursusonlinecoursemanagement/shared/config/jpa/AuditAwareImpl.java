package group.project.cursusonlinecoursemanagement.shared.config.jpa;

import group.project.cursusonlinecoursemanagement.user.domain.entity.User;
import group.project.cursusonlinecoursemanagement.user.service.UserService;
import group.project.cursusonlinecoursemanagement.user.service.impl.UserInfoDetails;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component(value = "auditAwareImpl")
public class AuditAwareImpl implements AuditorAware<User> {
    private final UserService userService;
    
    public AuditAwareImpl(UserService userService) {
        this.userService = userService;
    }
    
    /**
     * Returns the current auditor of the application.
     *
     * @return the current auditor.
     */
    @Override
    public Optional<User> getCurrentAuditor() {
        // Retrieve the current authentication from the security context.
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // If authentication exists, return the username of the authenticated user as the current auditor.
        if (authentication != null && authentication.isAuthenticated()) {
            UserInfoDetails userInfoDetails = (UserInfoDetails) authentication.getPrincipal();
            return Optional.of(userService.getUserFromRepoByEmail(userInfoDetails.getUsername()));
        }
        // Return null if no user is authenticated.
        return Optional.empty();
    }
}
