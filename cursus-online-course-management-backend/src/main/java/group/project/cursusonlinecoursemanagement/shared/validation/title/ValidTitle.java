package group.project.cursusonlinecoursemanagement.shared.validation.title;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Target({ ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = TitleValidator.class)
public @interface ValidTitle {
    int minLength() default 2;
    int maxLength() default 255;
    boolean nullable() default true;
    String message() default "Invalid Title!!";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
