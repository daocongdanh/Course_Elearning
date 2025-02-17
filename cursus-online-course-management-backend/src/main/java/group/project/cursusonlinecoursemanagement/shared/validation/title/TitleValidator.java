package group.project.cursusonlinecoursemanagement.shared.validation.title;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TitleValidator implements ConstraintValidator<ValidTitle, String> {
    private int minLength;
    private int maxLength;
    private boolean nullable;

    @Override
    public void initialize(ValidTitle constraintAnnotation) {
        this.minLength = constraintAnnotation.minLength();
        this.maxLength = constraintAnnotation.maxLength();
        this.nullable = constraintAnnotation.nullable();
    }
    @Override
    public boolean isValid(String title, ConstraintValidatorContext context) {
        try {
            if (nullable && title == null) { // Use the nullable field
                return true;
            }
            if (!nullable && title == null) { // Check for nullability
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("Title cannot be null").addConstraintViolation();
                return false;
            }

            if (title.length() < this.minLength || title.length() > this.maxLength) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("Must be between 2 and 50 characters").addConstraintViolation();
                return false;
            }

            if (!Character.isLetter(title.charAt(0))) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("Must start with a character").addConstraintViolation();
                return false;
            }

            if (!title.matches("[a-zA-Z0-9\\s\\p{Punct}\\p{So}]*")) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("Can only contain latin letters, digits, spaces, special symbols, and emoji").addConstraintViolation();
                return false;
            }
        }catch (Exception e) {
            throw new RuntimeException("Error validating: " + e.getMessage(), e);
        }
        return true;
    }
}