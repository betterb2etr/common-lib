package com.lxp.common.application.port.in;

/**
 * Command UseCase (CUD 작업)
 * 반환값이 없는 명령 처리용
 */
public interface CommandUseCase<I> {
    void execute(I command);
}
