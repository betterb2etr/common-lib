package com.lxp.common.domain.policy;

/**
 * Specification 패턴 인터페이스
 * 동적 쿼리 조건 조합에 사용
 *
 * @param <T> 검증 대상 타입
 */
public interface Specification<T> {

    /**
     * 조건 만족 여부 확인
     */
    boolean isSatisfiedBy(T entity);

    /**
     * AND 조합
     */
    default Specification<T> and(Specification<T> other) {
        return entity -> this.isSatisfiedBy(entity) && other.isSatisfiedBy(entity);
    }

    /**
     * OR 조합
     */
    default Specification<T> or(Specification<T> other) {
        return entity -> this.isSatisfiedBy(entity) || other.isSatisfiedBy(entity);
    }

    /**
     * NOT 조건
     */
    default Specification<T> not() {
        return entity -> !this.isSatisfiedBy(entity);
    }

    /**
     * 항상 true 반환
     */
    static <T> Specification<T> always() {
        return entity -> true;
    }

    /**
     * 항상 false 반환
     */
    static <T> Specification<T> never() {
        return entity -> false;
    }
}
