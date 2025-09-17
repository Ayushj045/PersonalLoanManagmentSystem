package com.nextGenZeta.LoanApplicationSystem.exception;

import lombok.Builder;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Data
@Builder
public class LoanApplyException extends RuntimeException {
    private final ErrorCode errorCode;
    private final Map<String, Object> additionalInfo = new HashMap<>();

    public LoanApplyException(ErrorCode errorCode) {
        super(errorCode.name());
        this.errorCode = errorCode;
    }

    public LoanApplyException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.name(), cause);
        this.errorCode = errorCode;
    }

    public LoanApplyException(ErrorCode errorCode, String msg) {
        super(msg);
        this.errorCode = errorCode;
    }

    public LoanApplyException(ErrorCode errorCode, String msg, Throwable cause) {
        super(msg, cause);
        this.errorCode = errorCode;
    }

    public LoanApplyException addAttributes(Map<String, Object> attributes) {
        if (Objects.isNull(attributes)) return this;
        this.additionalInfo.putAll(attributes);
        return this;
    }
}