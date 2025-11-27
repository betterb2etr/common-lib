package com.lxp.common.domain.policy;

/**
 * Specification 기본 구현체
 * 상속받아 구체적인 조건 구현
 *
 * @param <T> 검증 대상 타입
 */
public abstract class CompositeSpecification<T> implements Specification<T> {

    @Override
    public abstract boolean isSatisfiedBy(T entity);

    @Override
    public Specification<T> and(Specification<T> other) {
        return new AndSpecification<>(this, other);
    }

    @Override
    public Specification<T> or(Specification<T> other) {
        return new OrSpecification<>(this, other);
    }

    @Override
    public Specification<T> not() {
        return new NotSpecification<>(this);
    }

    private static class AndSpecification<T> extends CompositeSpecification<T> {
        private final Specification<T> left;
        private final Specification<T> right;

        AndSpecification(Specification<T> left, Specification<T> right) {
            this.left = left;
            this.right = right;
        }

        @Override
        public boolean isSatisfiedBy(T entity) {
            return left.isSatisfiedBy(entity) && right.isSatisfiedBy(entity);
        }
    }

    private static class OrSpecification<T> extends CompositeSpecification<T> {
        private final Specification<T> left;
        private final Specification<T> right;

        OrSpecification(Specification<T> left, Specification<T> right) {
            this.left = left;
            this.right = right;
        }

        @Override
        public boolean isSatisfiedBy(T entity) {
            return left.isSatisfiedBy(entity) || right.isSatisfiedBy(entity);
        }
    }

    private static class NotSpecification<T> extends CompositeSpecification<T> {
        private final Specification<T> spec;

        NotSpecification(Specification<T> spec) {
            this.spec = spec;
        }

        @Override
        public boolean isSatisfiedBy(T entity) {
            return !spec.isSatisfiedBy(entity);
        }
    }
}
