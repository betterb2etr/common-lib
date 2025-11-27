package com.lxp.common.infrastructure.exception;

import com.lxp.common.domain.exception.DomainException;

import java.time.LocalDateTime;

/**
 * API 에러 응답 DTO
 */
public class ErrorResponse {

    private final String code;
    private final String message;
    private final String group;
    private final LocalDateTime timestamp;

    public ErrorResponse(String code, String message, String group) {
        this.code = code;
        this.message = message;
        this.group = group;
        this.timestamp = LocalDateTime.now();
    }

    public static ErrorResponse from(DomainException exception) {
        return new ErrorResponse(
                exception.getCode(),
                exception.getMessage(),
                exception.getGroup()
        );
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getGroup() {
        return group;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
