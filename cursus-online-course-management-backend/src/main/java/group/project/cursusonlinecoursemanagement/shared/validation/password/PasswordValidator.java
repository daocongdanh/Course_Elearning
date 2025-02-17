package group.project.cursusonlinecoursemanagement.shared.validation.password;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {

    @Override
    public void initialize(ValidPassword constraintAnnotation) {}

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {

        if (password == null) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Must not be null").addConstraintViolation();
            return false;
        }
        if (password.contains(" ")) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Do not contain space, tab,...").addConstraintViolation();
            return false;
        }

        if (password.length() < 8||password.length() > 128) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Must be between 8 and 128 characters").addConstraintViolation();
            return false;
        }

        if (!password.matches(".*[A-Z].*")) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Include a capital letter").addConstraintViolation();
            return false;
        }

        if (!password.matches(".*[a-z].*")) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Include a lowercase letter").addConstraintViolation();
            return false;
        }

        if (!password.matches(".*\\d.*")) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Include a digit").addConstraintViolation();
            return false;
        }

        if (!password.matches(".*\\p{Punct}.*")) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Include a special symbol").addConstraintViolation();
            return false;
        }

        if (!password.matches("[a-zA-Z0-9\\p{Punct}]*")) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Invalid password!").addConstraintViolation();
            return false;
        }
        return true;
    }
}