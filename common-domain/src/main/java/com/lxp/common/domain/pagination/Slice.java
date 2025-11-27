package com.lxp.common.domain.pagination;

import java.util.List;
import java.util.function.Function;

/**
 * 슬라이스 결과 (전체 개수 없이 다음 페이지 존재 여부만 확인)
 * 무한 스크롤 등에 적합
 *
 * @param <T> 컨텐츠 타입
 */
public record Slice<T>(
        List<T> content,
        int pageNumber,
        int pageSize,
        boolean hasNext,
        boolean hasPrevious,
        boolean first,
        boolean last
) {

    public static <T> Slice<T> of(List<T> content, int pageNumber, int pageSize, boolean hasNext) {
        boolean first = pageNumber == 0;
        boolean hasPrevious = pageNumber > 0;
        boolean last = !hasNext;

        return new Slice<>(
                content,
                pageNumber,
                pageSize,
                hasNext,
                hasPrevious,
                first,
                last
        );
    }

    public static <T> Slice<T> empty(int pageSize) {
        return new Slice<>(
                List.of(),
                0,
                pageSize,
                false,
                false,
                true,
                true
        );
    }

    /**
     * 컨텐츠 타입 변환
     */
    public <U> Slice<U> map(Function<T, U> converter) {
        List<U> convertedContent = content.stream()
                .map(converter)
                .toList();

        return new Slice<>(
                convertedContent,
                pageNumber,
                pageSize,
                hasNext,
                hasPrevious,
                first,
                last
        );
    }

    public boolean isEmpty() {
        return content.isEmpty();
    }

    public int getNumberOfElements() {
        return content.size();
    }
}
