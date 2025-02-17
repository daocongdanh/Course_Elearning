package group.project.cursusonlinecoursemanagement.shared.permission.user;

import group.project.cursusonlinecoursemanagement.user.domain.entity.Status;
import group.project.cursusonlinecoursemanagement.user.service.UserService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class UserStatusAspect {

    @Autowired
    private UserService userService;

    @Around("@annotation(userStatus)")
    public Object around(ProceedingJoinPoint joinPoint, UserStatus userStatus) throws Throwable {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Status userStatusEnum = userService.findStatusByEmail(email);

        if (Arrays.asList(userStatus.unAllowedStatuses()).contains(userStatusEnum)) {
            throw new AccessDeniedException("Access denied");
        }

        return joinPoint.proceed();
    }
}
