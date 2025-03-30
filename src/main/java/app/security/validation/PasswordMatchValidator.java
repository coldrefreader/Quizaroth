package app.security.validation;


import app.web.dto.RegisterRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchValidator implements ConstraintValidator<PasswordMatch, RegisterRequest> {

    @Override
    public boolean isValid(RegisterRequest request, ConstraintValidatorContext context) {

        if (request.getPassword() == null) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Password is required")
                    .addPropertyNode("password")
                    .addConstraintViolation();
            return false;
        }

        if (request.getConfirmPassword() == null) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Confirm Password is required")
                    .addPropertyNode("password")
                    .addConstraintViolation();
            return false;
        }

        boolean isValid = request.getPassword().equals(request.getConfirmPassword());

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                    .addPropertyNode("confirmPassword")
                    .addConstraintViolation();
        }

        return isValid;
    }
}
