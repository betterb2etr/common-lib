package com.lxp.common.application.port.out.read;

import com.lxp.common.domain.pagination.Page;
import com.lxp.common.domain.pagination.PageRequest;

import java.util.Optional;

/**
 * CQRS Read Model 리포지토리 인터페이스
 * Query 측 데이터 조회 전용
 *
 * @param <T>  Read Model 타입
 * @param <ID> ID 타입
 */
public interface ReadModelRepository<T, ID> {

    /**
     * ID로 조회
     */
    Optional<T> findById(ID id);

    /**
     * 전체 조회 (페이징)
     */
    Page<T> findAll(PageRequest pageRequest);
}
