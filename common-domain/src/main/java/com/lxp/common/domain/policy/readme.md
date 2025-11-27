## 도메인 정책 사용예시

```java
public class VipDiscountPolicy implements DomainPolicy<Member> {
    public boolean isSatisfiedBy(Member member) {
        return member.isVip();
    }
}

// 복잡한 조건 조합 - Specification
Specification<Order> spec = new OrderStatusSpec(COMPLETED)
        .and(new AmountGreaterThanSpec(100000))
        .or(new VipOrderSpec());
```

## BusinessRule  사용예시
```java
// 사용 예시
public class OrderMustHaveItemsRule implements BusinessRule {
    
    private final Order order;
    
    public OrderMustHaveItemsRule(Order order) {
        this.order = order;
    }
    
    @Override
    public boolean isBroken() {
        return order.getItems().isEmpty();
    }
    
    @Override
    public String getMessage() {
        return "주문에는 최소 1개 이상의 상품이 필요합니다.";
    }
    
    @Override
    public ErrorCode getErrorCode() {
        return OrderErrorCode.EMPTY_ORDER_ITEMS;
    }
}
```

## 복잡한 규칙 조합
```java
// 깔끔하게 사용
BusinessRuleValidator.validateAll(
    new OrderMustHaveItemsRule(order),
    new OrderAmountMustBePositiveRule(order)
);
```