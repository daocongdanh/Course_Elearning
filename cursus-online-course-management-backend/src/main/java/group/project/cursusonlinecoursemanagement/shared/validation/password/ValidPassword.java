package group.project.cursusonlinecoursemanagement.shared.validation.password;

import java.lang.annotation.*;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Target({ ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordValidator.class)
public @interface ValidPassword {
    String message() default "Invalid password!!";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
