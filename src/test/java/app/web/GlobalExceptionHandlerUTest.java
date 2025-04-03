package app.web;

import app.exception.DomainException;
import app.web.dto.RegisterRequest;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.HttpStatus.*;

@ExtendWith(MockitoExtension.class)
public class GlobalExceptionHandlerUTest {

    private static Validator validator;
    private final GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testHandleValidationErrors() {

        RegisterRequest request = new RegisterRequest();
        request.setUsername("a");
        request.setPassword("validPassword");
        request.setConfirmPassword("validPassword");

        BindingResult bindingResult = new BeanPropertyBindingResult(request, "registerRequest");
        validator.validate(request).forEach(violation ->
                bindingResult.addError(new FieldError("registerRequest",
                        violation.getPropertyPath().toString(),
                        violation.getMessage())));


        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(null, bindingResult);
        ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleValidationErrors(exception);

        assertEquals(BAD_REQUEST, response.getStatusCode());

        Map<String, String> errors = (Map<String, String>) response.getBody().get("errors");
        assertEquals("Username length must be between 3 and 20 characters", errors.get("username"));
    }

    @Test
    void testHandleDomainErrors() {

        DomainException exception = new DomainException("User already exists");

        ResponseEntity<Map<String, String>> response = globalExceptionHandler.handleDomainException(exception);

        assertEquals(CONFLICT, response.getStatusCode());
        assertEquals("User already exists", Objects.requireNonNull(response.getBody()).get("errors"));
    }

    @Test
    void testHandleGenericException() {

        Exception exception = new Exception("Something went wrong");

        ResponseEntity<Map<String, String>> response = globalExceptionHandler.handleGenericException(exception);
        assertEquals(INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Something went wrong", Objects.requireNonNull(response.getBody()).get("errors"));
    }
}
