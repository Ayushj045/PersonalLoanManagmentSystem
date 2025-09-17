package com.nextGenZeta.LoanApplicationSystem.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.nextGenZeta.LoanApplicationSystem.exception.ErrorCode.DATA_INTEGRITY_ERROR;
import static com.nextGenZeta.LoanApplicationSystem.exception.ErrorCode.INTERNAL_SERVER_ERROR;
import static com.nextGenZeta.LoanApplicationSystem.exception.ErrorCode.METHOD_NOT_SUPPORTED;
import static com.nextGenZeta.LoanApplicationSystem.exception.ErrorCode.VALIDATION_ERROR;

@RestControllerAdvice
public class LoanApplyExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        Map<String, Object> additionalInfo = new LinkedHashMap<>();
        List<FieldError> fieldErrorList = ex.getBindingResult().getFieldErrors();
        for (int i = 0; i < fieldErrorList.size(); i++) {
            FieldError error = fieldErrorList.get(i);
            String message = String.format("%s => %s", error.getField(), error.getDefaultMessage());
            String key = String.format("Violation %d", i + 1);
            additionalInfo.put(key, message);
        }
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message("Provided arguments are not valid.")
                .errorCode(VALIDATION_ERROR.getCode())
                .errorDescription("Validation failed for request parameters.")
                .additionalInfo(additionalInfo)
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(METHOD_NOT_SUPPORTED.getCode())
                .errorDescription("HTTP method not supported for this endpoint.")
                .message(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(errorResponse);
    }

    @ExceptionHandler(LoanApplyException.class)
    public ResponseEntity<ErrorResponse> handleLoanApplyException(LoanApplyException ex) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(ex.getErrorCode().getCode())
                .errorDescription(ex.getErrorCode().getDesc())
                .message(ex.getMessage())
                .additionalInfo(ex.getAdditionalInfo())
                .build();
        return ResponseEntity.status(ex.getErrorCode().getStatus()).body(errorResponse);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(DATA_INTEGRITY_ERROR.getCode())
                .errorDescription("Data integrity violation.")
                .message("Invalid loanId -> referenced loan does not exist.")
                .additionalInfo(Map.of("detail", ex.getMostSpecificCause().getMessage()))
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleInternalServerError(Exception ex) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(INTERNAL_SERVER_ERROR.getCode())
                .errorDescription("An unexpected error occurred.")
                .message(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

}