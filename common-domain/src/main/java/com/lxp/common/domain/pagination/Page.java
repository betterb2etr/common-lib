package com.lxp.common.domain.pagination;

import java.util.List;
import java.util.function.Function;

/**
 * 페이지네이션 결과 (Spring 의존 없음)
 *
 * @param <T> 컨텐츠 타입
 */
public record Page<T>(
        List<T> content,
        int pageNumber,
        int pageSize,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last,
        boolean hasNext,
        boolean hasPrevious
) {

    public static <T> Page<T> of(List<T> content, int pageNumber, int pageSize, long totalElements) {
        int totalPages = (int) Math.ceil((double) totalElements / pageSize);
        boolean first = pageNumber == 0;
        boolean last = pageNumber >= totalPages - 1;
        boolean hasNext = pageNumber < totalPages - 1;
        boolean hasPrevious = pageNumber > 0;

        return new Page<>(
                content,
                pageNumber,
                pageSize,
                totalElements,
                totalPages,
                first,
                last,
                hasNext,
                hasPrevious
        );
    }

    public static <T> Page<T> empty(int pageSize) {
        return new Page<>(
                List.of(),
                0,
                pageSize,
                0,
                0,
                true,
                true,
                false,
                false
        );
    }

    /**
     * 컨텐츠 타입 변환
     */
    public <U> Page<U> map(Function<T, U> converter) {
        List<U> convertedContent = content.stream()
                .map(converter)
                .toList();

        return new Page<>(
                convertedContent,
                pageNumber,
                pageSize,
                totalElements,
                totalPages,
                first,
                last,
                hasNext,
                hasPrevious
        );
    }

    public boolean isEmpty() {
        return content.isEmpty();
    }

    public int getNumberOfElements() {
        return content.size();
    }
}
