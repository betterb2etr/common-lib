package com.lxp.common.domain.exception;

/**
 * 에러 코드 인터페이스
 * 각 도메인에서 Enum으로 구현하여 사용
 */
public interface ErrorCode {

    String getCode();


    String getMessage();

    /**
     * 에러 그룹 (HTTP 상태 매핑 등에 사용)
     * 예: "NOT_FOUND", "BAD_REQUEST", "CONFLICT"
     */
    default String getGroup() {
        return "INTERNAL_ERROR";
    }
}
