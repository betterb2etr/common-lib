package com.lxp.common.infrastructure.exception;

import com.lxp.common.domain.exception.DomainException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 전역 예외 처리기
 * DomainException을 HTTP 응답으로 변환
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ErrorResponse> handleDomainException(DomainException exception) {
        HttpStatus status = mapToHttpStatus(exception.getGroup());
        ErrorResponse response = ErrorResponse.from(exception);
        return ResponseEntity.status(status).body(response);
    }

    private HttpStatus mapToHttpStatus(String group) {
        if (group == null) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
        
        return switch (group.toUpperCase()) {
            case "NOT_FOUND" -> HttpStatus.NOT_FOUND;
            case "BAD_REQUEST", "INVALID" -> HttpStatus.BAD_REQUEST;
            case "CONFLICT" -> HttpStatus.CONFLICT;
            case "FORBIDDEN" -> HttpStatus.FORBIDDEN;
            case "UNAUTHORIZED" -> HttpStatus.UNAUTHORIZED;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }
}
