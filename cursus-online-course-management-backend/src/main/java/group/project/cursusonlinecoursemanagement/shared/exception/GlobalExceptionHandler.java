package group.project.cursusonlinecoursemanagement.shared.exception;

import group.project.cursusonlinecoursemanagement.shared.domain.ApiResponse;
import group.project.cursusonlinecoursemanagement.shared.domain.ErrorDetail;
import group.project.cursusonlinecoursemanagement.shared.exception.handler.*;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles any unhandled exceptions that are not specifically caught by other exception handlers.
     *
     * @param exception  The exception that occurred
     * @param webRequest The original WebRequest
     * @return An HTTP response containing error details and status code
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ApiResponse<ErrorDetail>> handleUnHandledException(Exception exception, WebRequest webRequest) {
        ErrorDetail errorDetail = new ErrorDetail(
                new Date(),
                exception.getMessage(),
                webRequest.getDescription(false)
        );
        ApiResponse<ErrorDetail> response = ApiResponse.<ErrorDetail>builder()
                .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message("Internal Server Error")
                .data(errorDetail)
                .build();
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handles exceptions of type AppExceptionHandler.
     * Returns an ApiResponse object with details about the exception.
     *
     * @param ex      The AppExceptionHandler exception that occurred.
     * @param request The original WebRequest.
     * @return An ApiResponse object containing details about the exception.
     */
    @ExceptionHandler(AppExceptionHandler.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<ErrorMessage> handleAppException(AppExceptionHandler ex, WebRequest request) {
        ErrorMessage message = new ErrorMessage(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                new Date(),
                ex.getMessage(),
                request.getDescription(false));
        return ApiResponse.<ErrorMessage>builder()
                .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message("Application Error")
                .data(message)
                .build();
    }

    /**
     * Handles exceptions of type MethodArgumentNotValidException.
     * Returns a ResponseEntity object with a map of field errors.
     *
     * @param exception The MethodArgumentNotValidException that occurred.
     * @return A ResponseEntity object containing a map of field errors.
     */
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ResponseEntity<ApiResponse<Map<String, String>>> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        Map<String, String> errors = new HashMap<>();
        BindingResult bindingResult = exception.getBindingResult();
        bindingResult.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
        ApiResponse<Map<String, String>> response = ApiResponse.<Map<String, String>>builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .message("Validation Error")
                .data(errors)
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Handles exceptions of type ResourceNotFoundException.
     * Returns a ResponseEntity object with the exception message.
     *
     * @param exception The ResourceNotFoundException that occurred.
     * @return A ResponseEntity object containing the exception message.
     */
    @ExceptionHandler(value = ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    ResponseEntity<ApiResponse<String>> handleResourceNotFoundException(ResourceNotFoundException exception) {
        ApiResponse<String> response = ApiResponse.<String>builder()
                .code(HttpStatus.NOT_FOUND.value())
                .message(exception.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    /**
     * Handles exceptions of type DuplicateFieldException.
     * Returns a ResponseEntity object with the exception message.
     *
     * @param exception The DuplicateFieldException that occurred.
     * @return A ResponseEntity object containing the exception message.
     */
    @ExceptionHandler(value = DuplicateFieldException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ResponseEntity<ApiResponse<String>> handleDuplicateFieldException(DuplicateFieldException exception) {
        ApiResponse<String> response = ApiResponse.<String>builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .message(exception.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Handles exceptions of type ExpiredAccessException.
     * This exception is typically thrown when a user's access has expired.
     *
     * @param exception The ExpiredAccessException that occurred.
     * @return A ResponseEntity object containing an ApiResponse with the exception message.
     */
    @ExceptionHandler(value = ExpiredAccessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ResponseEntity<ApiResponse<String>> handleExpiledAccessException(ExpiredAccessException exception) {
        ApiResponse<String> response = ApiResponse.<String>builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .message(exception.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Handles exceptions of type ConstraintViolationException.
     * This exception is thrown when a constraint on a field is violated, typically during validation.
     *
     * @param exception The ConstraintViolationException that occurred.
     * @return A ResponseEntity object containing an ApiResponse with a map of field errors.
     */
    @ExceptionHandler(value = ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<String>> handleConstraintViolationException(ConstraintViolationException exception) {
        Map<String, String> errors = exception.getConstraintViolations().stream()
                .collect(Collectors.toMap(
                        violation -> violation.getPropertyPath().toString(),
                        ConstraintViolation::getMessage
                ));
        ApiResponse<String> response = ApiResponse.<String>builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .dataMap(errors)
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Handles exceptions of type MaxUploadSizeExceededException.
     * This exception is thrown when an uploaded file exceeds the maximum allowed size.
     *
     * @param exception The MaxUploadSizeExceededException that occurred.
     * @return A ResponseEntity object containing an ApiResponse with the exception message.
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiResponse<String>> handleMaxSizeException(MaxUploadSizeExceededException exception) {
        ApiResponse<String> response = ApiResponse.<String>builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .message(exception.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

        /**
     * Handles exceptions of type HttpMediaTypeNotAcceptableException.
     * This exception is thrown when the client sends a request with an Accept header
     * that doesn't match any of the media types that the server can produce.
     *
     * @param ex The HttpMediaTypeNotAcceptableException that occurred.
     * @return A ResponseEntity object containing details about the unsupported media type error.
     */
    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    public ResponseEntity<ApiResponse<String>> handleHttpMediaTypeNotAcceptableException(HttpMediaTypeNotAcceptableException ex) {
        ApiResponse<String> response = ApiResponse.<String>builder()
                .code(HttpStatus.NOT_ACCEPTABLE.value())
                .message("Unsupported Media Type")
                .data("The server cannot produce a response matching the list of acceptable values defined in the request's proactive content negotiation headers")
                .build();
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(response);
    }
}
