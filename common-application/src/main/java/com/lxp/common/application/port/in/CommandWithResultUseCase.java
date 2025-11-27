package com.lxp.common.application.port.in;

/**
 * Command UseCase with Result (CUD 작업 + 결과 반환)
 * 생성 후 ID 반환 등에 사용
 */
public interface CommandWithResultUseCase<I, O> {
    O execute(I command);
}
