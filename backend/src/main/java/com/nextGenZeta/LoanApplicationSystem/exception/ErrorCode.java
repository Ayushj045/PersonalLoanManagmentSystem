package com.nextGenZeta.LoanApplicationSystem.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    LOAN_NOT_FOUND("LOAN_001", "Loan not found", HttpStatus.NOT_FOUND),
    USER_NOT_FOUND("USER_001", "User not found", HttpStatus.NOT_FOUND),
    RESOURCE_ALREADY_EXISTS("RESOURCE_001", "Resource already exists", HttpStatus.CONFLICT),
    VALIDATION_ERROR("VALIDATION_001", "Validation failed for request parameters", HttpStatus.BAD_REQUEST),
    DATA_INTEGRITY_ERROR("DATA_001", "Data integrity violation", HttpStatus.BAD_REQUEST),
    METHOD_NOT_SUPPORTED("METHOD_001", "HTTP method not supported", HttpStatus.METHOD_NOT_ALLOWED),
    INTERNAL_SERVER_ERROR("SERVER_001", "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR),
    LOAN_APPLY_ERROR("LOAN_002", "Loan application error occurred", HttpStatus.BAD_REQUEST),
    AUTH_UNAUTHORIZED("AUTH_002", "Unauthorized access", HttpStatus.UNAUTHORIZED),
    AUTH_FORBIDDEN("AUTH_003", "Forbidden", HttpStatus.FORBIDDEN),
    RESOURCE_NOT_FOUND("RESOURCE_002", "Resource not found", HttpStatus.NOT_FOUND),
    INVALID_CREDENTIALS("AUTH_001", "Invalid credentials", HttpStatus.UNAUTHORIZED),
    AUTH_TOKEN_EXPIRED("AUTH_004", "Authentication token expired", HttpStatus.UNAUTHORIZED),
    DUPLICATE_LOAN_REQUEST("LOAN_003", "Duplicate loan request within 24 hours", HttpStatus.BAD_REQUEST);
    private final String code;
    private final String desc;
    private final HttpStatus status;

    ErrorCode(String code, String desc, HttpStatus status) {
        this.code = code;
        this.desc = desc;
        this.status = status;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public HttpStatus getStatus() {
        return status;
    }
}