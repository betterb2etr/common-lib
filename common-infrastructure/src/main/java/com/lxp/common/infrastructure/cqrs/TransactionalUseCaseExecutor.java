package com.lxp.common.infrastructure.cqrs;

import com.lxp.common.application.port.in.CommandUseCase;
import com.lxp.common.application.port.in.CommandWithResultUseCase;
import com.lxp.common.application.port.in.QueryUseCase;
import com.lxp.common.application.port.in.UseCase;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 트랜잭션 UseCase 실행기
 * UseCase 실행 시 트랜잭션 경계 설정
 */
public class TransactionalUseCaseExecutor {

    /**
     * 일반 UseCase 실행 (트랜잭션)
     */
    @Transactional
    public <I, O> O execute(UseCase<I, O> useCase, I input) {
        return useCase.execute(input);
    }

    /**
     * Command UseCase 실행 (트랜잭션)
     */
    @Transactional
    public <I> void executeCommand(CommandUseCase<I> useCase, I input) {
        useCase.execute(input);
    }

    /**
     * Command with Result UseCase 실행 (트랜잭션)
     */
    @Transactional
    public <I, O> O executeCommandWithResult(CommandWithResultUseCase<I, O> useCase, I input) {
        return useCase.execute(input);
    }

    /**
     * Query UseCase 실행 (읽기 전용 트랜잭션)
     */
    @Transactional(readOnly = true)
    public <I, O> O executeQuery(QueryUseCase<I, O> useCase, I input) {
        return useCase.execute(input);
    }

    /**
     * 새 트랜잭션에서 UseCase 실행
     */
    @Transactional(propagation = org.springframework.transaction.annotation.Propagation.REQUIRES_NEW)
    public <I, O> O executeInNewTransaction(UseCase<I, O> useCase, I input) {
        return useCase.execute(input);
    }
}
