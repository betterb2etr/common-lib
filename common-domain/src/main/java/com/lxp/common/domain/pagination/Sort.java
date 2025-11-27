package com.lxp.common.domain.pagination;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 정렬 정보
 */
public record Sort(List<Order> orders) {

    public static Sort unsorted() {
        return new Sort(Collections.emptyList());
    }

    public static Sort by(String... properties) {
        List<Order> orders = new ArrayList<>();
        for (String property : properties) {
            orders.add(Order.asc(property));
        }
        return new Sort(orders);
    }

    public static Sort by(Direction direction, String... properties) {
        List<Order> orders = new ArrayList<>();
        for (String property : properties) {
            orders.add(new Order(direction, property));
        }
        return new Sort(orders);
    }

    public static Sort by(Order... orders) {
        return new Sort(List.of(orders));
    }

    public Sort and(Sort other) {
        List<Order> combined = new ArrayList<>(this.orders);
        combined.addAll(other.orders);
        return new Sort(combined);
    }

    public boolean isSorted() {
        return !orders.isEmpty();
    }

    public boolean isUnsorted() {
        return orders.isEmpty();
    }

    public enum Direction {
        ASC, DESC
    }

    public record Order(Direction direction, String property) {

        public static Order asc(String property) {
            return new Order(Direction.ASC, property);
        }

        public static Order desc(String property) {
            return new Order(Direction.DESC, property);
        }

        public boolean isAscending() {
            return direction == Direction.ASC;
        }

        public boolean isDescending() {
            return direction == Direction.DESC;
        }
    }
}
