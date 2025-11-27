package com.lxp.common.domain.pagination;

/**
 * 페이지 요청 정보
 */
public record PageRequest(
        int pageNumber,
        int pageSize,
        Sort sort
) {

    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final int MAX_PAGE_SIZE = 100;

    public PageRequest {
        if (pageNumber < 0) {
            throw new IllegalArgumentException("Page number must not be negative");
        }
        if (pageSize < 1) {
            throw new IllegalArgumentException("Page size must be at least 1");
        }
        if (pageSize > MAX_PAGE_SIZE) {
            pageSize = MAX_PAGE_SIZE;
        }
    }

    public static PageRequest of(int pageNumber, int pageSize) {
        return new PageRequest(pageNumber, pageSize, Sort.unsorted());
    }

    public static PageRequest of(int pageNumber, int pageSize, Sort sort) {
        return new PageRequest(pageNumber, pageSize, sort);
    }

    public static PageRequest first(int pageSize) {
        return of(0, pageSize);
    }

    public static PageRequest first() {
        return of(0, DEFAULT_PAGE_SIZE);
    }

    public long getOffset() {
        return (long) pageNumber * pageSize;
    }

    public PageRequest next() {
        return new PageRequest(pageNumber + 1, pageSize, sort);
    }

    public PageRequest previous() {
        return pageNumber == 0 ? this : new PageRequest(pageNumber - 1, pageSize, sort);
    }

    public PageRequest withSort(Sort sort) {
        return new PageRequest(pageNumber, pageSize, sort);
    }
}
