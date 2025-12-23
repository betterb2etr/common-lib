## 🛡️ LXP Common Domain Module Guide

`common-domain` 모듈은 프로젝트의 가장 안쪽에 위치하며, **비즈니스 로직의 순수성을 보장**하고 **도메인 모델의 표준**을 정의합니다. 이 모듈은 Spring Framework나 JPA와 같은 외부 기술에 의존하지 않으므로, 언제든 테스트 가능하고 이식 가능한 코드를 작성할 수 있습니다.

### 1. 도메인 모델링 (Modeling)

엔티티와 값 객체(VO)를 정의하기 위한 기본 클래스입니다.

| 클래스 | 용도 | 특징 |
| --- | --- | --- |
| **`BaseEntity<ID>`** | 식별자(ID)가 있는 엔티티의 최상위 클래스 | `equals()`/`hashCode()`가 ID 기준으로 자동 구현됨. |
| **`ValueObject`** | 식별자 없이 속성 값으로만 식별되는 객체 | `getEqualityComponents()`만 구현하면 값 비교 로직 자동 완성. |
| **`AggregateRoot`** | 트랜잭션의 일관성 경계를 정의하는 루트 엔티티 | 도메인 이벤트 발행 및 라이프사이클 관리 기능(`registerEvent`) 내장. |

Usage Example: 안전한 Value Object 만들기

`ValueObject`를 상속받으면 모든 필드를 일일이 비교하는 `equals` 코드를 짤 필요가 없습니다.

```java
public class Money extends ValueObject {
    private final BigDecimal amount;
    private final String currency;

    // 생성자 생략...

    @Override
    protected Object[] getEqualityComponents() {
        // 이 필드들이 같으면 같은 객체로 취급합니다.
        return new Object[]{amount, currency};
    }
}
```

---

### 2. 비즈니스 규칙 검증 (Validation)

`if (price < 0) throw ...` 와 같은 절차적 검증 코드를 **객체지향적인 규칙(Rule)**으로 캡슐화합니다.

### 🛠️ Quick Start: 비즈니스 룰 적용하기

**1. 규칙 정의 (`BusinessRule` 구현)**

```java
public class NotEnoughStockRule implements BusinessRule {
    private final int currentStock;
    private final int requestQuantity;

    public NotEnoughStockRule(int currentStock, int requestQuantity) {
        this.currentStock = currentStock;
        this.requestQuantity = requestQuantity;
    }

    @Override
    public boolean isBroken() {
        return currentStock < requestQuantity; // 재고 부족 시 true (규칙 위반)
    }

    @Override
    public String getMessage() {
        return "재고가 부족합니다.";
    }

    @Override
    public ErrorCode getErrorCode() {
        return InventoryErrorCode.OUT_OF_STOCK;
    }
}
```

**2. 도메인 로직에서 검증 수행 (`BusinessRuleValidator`)**

```java
public void decreaseStock(int quantity) {
    // 여러 규칙을 한 번에 검증할 수 있습니다.
    BusinessRuleValidator.validateAll(
        new NotEnoughStockRule(this.stock, quantity),
        new OrderLimitRule(quantity)
    );
    
    this.stock -= quantity;
}
```

---

### 3. 정책 및 조건 조합 (Specification)

복잡한 조건문(필터링, 정책 등)을 작은 단위의 `Specification`으로 나누고 조립하여 사용합니다.

🚀 Use Case: 복잡한 할인 대상 선정 로직

```java
// 개별 조건 정의
Specification<Member> isVip = member -> member.getLevel() == Level.VIP;
Specification<Member> hasCoupon = member -> member.hasActiveCoupon();
Specification<Member> isBlacklisted = member -> member.getStatus() == Status.BLOCK;

// 조건 조합 (VIP이거나 쿠폰이 있고, 블랙리스트가 아닌 회원)
Specification<Member> discountTargetSpec = isVip
    .or(hasCoupon)
    .and(isBlacklisted.not());

if (discountTargetSpec.isSatisfiedBy(currentMember)) {
    applyDiscount();
}
```

---

### 4. 프레임워크 독립적인 페이지네이션

Spring Data의 `Page` 객체에 의존하지 않고, 순수 도메인 로직에서도 페이징 처리를 할 수 있도록 지원합니다.

- **`Page<T>`**: 전체 카운트(`totalElements`)가 필요한 일반적인 게시판 형태 페이징.
- **`Slice<T>`**: 전체 카운트 없이 "다음 페이지 존재 여부(`hasNext`)"만 필요한 무한 스크롤 형태.
- **`PageRequest`**: 페이지 번호(0부터 시작), 사이즈, 정렬(`Sort`) 정보를 담은 요청 객체.

> Note: Adapter 계층(Controller/Repository)에서 Spring의 Page 객체를 이 도메인 Page 객체로 변환하여 Service 계층으로 전달해야 합니다.
>

---

✅ Check Point

- [ ]  모든 Entity는 `BaseEntity`를 상속받아 ID 기반의 `equals`를 보장받고 있는가?
- [ ]  단순 값들의 묶음(주소, 좌표 등)은 `ValueObject`로 정의했는가?
- [ ]  비즈니스 규칙 위반 시 `throw new RuntimeException(...)` 대신 `BusinessRule`을 정의하여 검증하고 있는가?
- [ ]  도메인 로직 내부에 `org.springframework.*` 패키지 import가 없는지 확인했는가?

---