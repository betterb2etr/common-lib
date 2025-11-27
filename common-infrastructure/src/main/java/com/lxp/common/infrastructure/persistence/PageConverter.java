package com.lxp.common.infrastructure.persistence;

import com.lxp.common.domain.pagination.Page;
import com.lxp.common.domain.pagination.PageRequest;
import com.lxp.common.domain.pagination.Slice;
import com.lxp.common.domain.pagination.Sort;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

/**
 * 도메인 페이지네이션 ↔ Spring Data 페이지네이션 변환 유틸리티
 */
public final class PageConverter {

    private PageConverter() {
    }

    /**
     * 도메인 PageRequest → Spring Pageable
     */
    public static Pageable toSpringPageable(PageRequest pageRequest) {
        return org.springframework.data.domain.PageRequest.of(
                pageRequest.pageNumber(),
                pageRequest.pageSize(),
                toSpringSort(pageRequest.sort())
        );
    }

    /**
     * 도메인 Sort → Spring Sort
     */
    public static org.springframework.data.domain.Sort toSpringSort(Sort sort) {
        if (sort == null || sort.isUnsorted()) {
            return org.springframework.data.domain.Sort.unsorted();
        }

        List<org.springframework.data.domain.Sort.Order> orders = new ArrayList<>();
        for (Sort.Order order : sort.orders()) {
            org.springframework.data.domain.Sort.Direction direction =
                    order.isAscending()
                            ? org.springframework.data.domain.Sort.Direction.ASC
                            : org.springframework.data.domain.Sort.Direction.DESC;
            orders.add(new org.springframework.data.domain.Sort.Order(direction, order.property()));
        }

        return org.springframework.data.domain.Sort.by(orders);
    }

    /**
     * Spring Page → 도메인 Page
     */
    public static <T> Page<T> toDomainPage(org.springframework.data.domain.Page<T> springPage) {
        return Page.of(
                springPage.getContent(),
                springPage.getNumber(),
                springPage.getSize(),
                springPage.getTotalElements()
        );
    }

    /**
     * Spring Slice → 도메인 Slice
     */
    public static <T> Slice<T> toDomainSlice(org.springframework.data.domain.Slice<T> springSlice) {
        return Slice.of(
                springSlice.getContent(),
                springSlice.getNumber(),
                springSlice.getSize(),
                springSlice.hasNext()
        );
    }

    /**
     * Spring Pageable → 도메인 PageRequest
     */
    public static PageRequest toDomainPageRequest(Pageable pageable) {
        return PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                toDomainSort(pageable.getSort())
        );
    }

    /**
     * Spring Sort → 도메인 Sort
     */
    public static Sort toDomainSort(org.springframework.data.domain.Sort springSort) {
        if (springSort == null || springSort.isUnsorted()) {
            return Sort.unsorted();
        }

        List<Sort.Order> orders = new ArrayList<>();
        for (org.springframework.data.domain.Sort.Order springOrder : springSort) {
            Sort.Direction direction = springOrder.isAscending()
                    ? Sort.Direction.ASC
                    : Sort.Direction.DESC;
            orders.add(new Sort.Order(direction, springOrder.getProperty()));
        }

        return new Sort(orders);
    }
}
