package com.lxp.common.application.port.out;

import java.util.List;

/**
 * 도메인 ↔ 영속성 엔티티 매퍼 인터페이스
 *
 * @param <D> Domain 모델
 * @param <E> Entity (JPA 등)
 */
public interface DomainMapper<D, E> {

    /**
     * 영속성 엔티티 → 도메인 모델 변환
     */
    D toDomain(E entity);

    /**
     * 도메인 모델 → 영속성 엔티티 변환
     */
    E toEntity(D domain);

    /**
     * 영속성 엔티티 목록 → 도메인 모델 목록 변환
     */
    default List<D> toDomainList(List<E> entities) {
        if (entities == null) {
            return List.of();
        }
        return entities.stream()
                .map(this::toDomain)
                .toList();
    }

    /**
     * 도메인 모델 목록 → 영속성 엔티티 목록 변환
     */
    default List<E> toEntityList(List<D> domains) {
        if (domains == null) {
            return List.of();
        }
        return domains.stream()
                .map(this::toEntity)
                .toList();
    }
}
