package group.project.cursusonlinecoursemanagement.shared.permission.user;

import group.project.cursusonlinecoursemanagement.user.domain.entity.Status;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface UserStatus {
    Status[] unAllowedStatuses();
}
