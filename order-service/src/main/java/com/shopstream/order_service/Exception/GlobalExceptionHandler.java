package com.shopstream.order_service.Exception;




import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.shopstream.order_service.dto.ErrorResponse;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1) Bean validation errors for @Valid @RequestBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgNotValid(MethodArgumentNotValidException ex) {
        BindingResult br = ex.getBindingResult();
        List<ErrorResponse.FieldError> fieldErrors = br.getFieldErrors().stream()
                .map(fe -> new ErrorResponse.FieldError(fe.getField(), fe.getDefaultMessage()))
                .collect(Collectors.toList());

        ErrorResponse err = new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
                "Validation Failed",
                "One or more fields are invalid");
        err.setFieldErrors(fieldErrors);

        return new ResponseEntity<>(err, HttpStatus.BAD_REQUEST);
    }

    // 2) Constraint violations (e.g. @Validated on method params)
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex) {
        List<ErrorResponse.FieldError> fieldErrors = ex.getConstraintViolations().stream()
                .map(cv -> new ErrorResponse.FieldError(
                        // propertyPath like "createProduct.req.name", extract last node
                        getLastNode(cv),
                        cv.getMessage()))
                .collect(Collectors.toList());

        ErrorResponse err = new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
                "Constraint Violation",
                "Validation failed for request parameters");
        err.setFieldErrors(fieldErrors);

        return new ResponseEntity<>(err, HttpStatus.BAD_REQUEST);
    }

    private String getLastNode(ConstraintViolation<?> cv) {
        String path = cv.getPropertyPath().toString();
        if (path == null) return "";
        if (!path.contains(".")) return path;
        String[] parts = path.split("\\.");
        return parts[parts.length - 1];
    }

    // 3) Malformed JSON / missing body
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        ErrorResponse err = new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
                "Malformed Request",
                ex.getMostSpecificCause() != null ? ex.getMostSpecificCause().getMessage() : ex.getMessage());
        return new ResponseEntity<>(err, HttpStatus.BAD_REQUEST);
    }

    // 4) Generic runtime exceptions (fallback)
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntime(RuntimeException ex) {
        ErrorResponse err = new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                ex.getMessage());
        return new ResponseEntity<>(err, HttpStatus.BAD_REQUEST);
    }

    // optionally handle other statuses (404, 401) elsewhere or throw ResponseStatusException
}
