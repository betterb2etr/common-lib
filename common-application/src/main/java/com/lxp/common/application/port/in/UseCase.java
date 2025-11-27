package com.lxp.common.application.port.in;

/**
 * UseCase 기본 인터페이스
 * Input Port 마커
 */
public interface UseCase<I, O> {
    O execute(I input);
}
