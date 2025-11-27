package com.lxp.common.domain.model;

import java.util.Objects;

/**
 * 엔티티 기본 클래스
 * ID 기반 동등성 비교 제공
 */
public abstract class BaseEntity<ID> {

    public abstract ID getId();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        BaseEntity<?> that = (BaseEntity<?>) o;

        if (getId() == null || that.getId() == null) {
            return false;
        }
        
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return getId() != null ? getId().hashCode() : 0;
    }
}
