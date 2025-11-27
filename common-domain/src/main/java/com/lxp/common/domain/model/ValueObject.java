package com.lxp.common.domain.model;

/**
 * 값 객체 기본 클래스
 * 속성 기반 동등성 비교를 위해 equals/hashCode 구현 필요
 */
public abstract class ValueObject {

    /**
     * 동등성 비교를 위한 속성 목록 반환
     * 하위 클래스에서 구현 필요
     */
    protected abstract Object[] getEqualityComponents();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ValueObject that = (ValueObject) o;

        Object[] thisComponents = this.getEqualityComponents();
        Object[] thatComponents = that.getEqualityComponents();

        if (thisComponents.length != thatComponents.length) {
            return false;
        }

        for (int i = 0; i < thisComponents.length; i++) {
            if (thisComponents[i] == null && thatComponents[i] == null) {
                continue;
            }
            if (thisComponents[i] == null || !thisComponents[i].equals(thatComponents[i])) {
                return false;
            }
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = 0;
        for (Object component : getEqualityComponents()) {
            result = 31 * result + (component != null ? component.hashCode() : 0);
        }
        return result;
    }
}
