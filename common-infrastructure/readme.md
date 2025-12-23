## 🏗️ LXP Common Infrastructure Module Guide

`common-infrastructure` 모듈은 `common-domain`과 `common-application`에서 정의한 인터페이스를 **Spring Boot 기술 스택으로 구현**한 구현체 집합입니다.

개발자는 이 모듈을 통해 **반복적인 설정(Boilerplate)을 줄이고**, **표준화된 응답 및 에러 처리**를 자동으로 적용받을 수 있습니다.

1. 표준 웹 응답 및 에러 처리 (Web Standard)

REST API의 성공/실패 응답 포맷을 통일합니다. 컨트롤러에서 `ResponseEntity`를 직접 조립하는 대신 아래 표준을 따르십시오.

🚀 Usage: 성공 응답 반환

```java
`ApiResponse.success(data)`를 사용하여 일관된 JSON 구조를 반환합니다.

`@GetMapping("/{id}")
public ApiResponse<MemberDto> getMember(@PathVariable Long id) {
    MemberDto member = queryService.getMember(id);
    // return type: { "success": true, "data": { ... }, "timestamp": "..." }
    return ApiResponse.success(member);
}`
```

🚀 Usage: 에러 처리 (Global Exception Handler)

`try-catch`로 에러를 잡아서 응답을 만들지 마십시오. **비즈니스 예외(`DomainException`)를 던지면** 핸들러가 자동으로 HTTP 상태 코드와 메시지를 매핑합니다.

| Error Group | HTTP Status | 예시 상황 |
| --- | --- | --- |
| `NOT_FOUND` | 404 Not Found | 리소스 없음 |
| `BAD_REQUEST` | 400 Bad Request | 입력값 검증 실패 |
| `CONFLICT` | 409 Conflict | 중복 데이터, 상태 충돌 |
| `FORBIDDEN` | 403 Forbidden | 권한 없음 |

```java
// Service Layer
if (emailRepository.exists(email)) {
    // 409 Conflict로 자동 변환됨
    throw new MemberDomainException(MemberErrorCode.EMAIL_DUPLICATION);
}
```

---

2. 데이터 영속성 (Persistence & JPA)

JPA Entity 정의 시 반복되는 코드(ID, 생성일, 수정일 등)를 제거하고, 감사(Auditing) 기능을 제공합니다.

### 🛠️ Base Entity 선택 가이드

| 클래스 | 용도 | 포함 필드 |
| --- | --- | --- |
| **`BaseJpaEntity`** | 일반적인 엔티티 | `id(Long)`, `createdAt`, `updatedAt` |
| **`BaseSoftDeleteJpaEntity`** | 삭제 이력이 남아야 하는 중요 데이터 | 위 필드 + `deleted(bool)`, `deletedAt`, `delete()`, `restore()` |
| **`BaseUuidJpaEntity`** | 보안상 UUID가 필요한 엔티티 | `id(String-UUID)`, `createdAt`, `updatedAt` |
| **`BaseVersionedJpaEntity`** | 동시성 제어가 필요한 엔티티 (낙관적 락) | `id(Long)`, `version(Long)`, `createdAt`, `updatedAt` |

💡 Example: Soft Delete Entity 구현

```java
@Entity
@Table(name = "courses")
@SQLRestriction("is_deleted = false") // 조회 시 삭제된 데이터 자동 제외
public class CourseEntity extends BaseSoftDeleteJpaEntity {
    // ... 필드 정의
}

// 삭제 시
courseRepository.findById(1L).ifPresent(entity -> {
    entity.delete(); // DB 삭제 대신 deleted=true 업데이트
});
```

---

### 3. 페이지네이션 변환 (Page Converter)

도메인 영역의 `Page`(POJO)와 Spring Data JPA의 `Page`(Framework) 사이를 이어주는 브릿지입니다.

🚀 Usage: Controller/Service 간 변환

```java
// 1. Controller (Request DTO -> Domain PageRequest)
@GetMapping
public ApiResponse<Page<CourseDto>> search(
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "20") int size
) {
    PageRequest domainRequest = PageRequest.of(page, size);
    return ApiResponse.success(service.search(domainRequest));
}

// 2. Adapter (Domain PageRequest -> Spring Pageable -> Domain Page)
@Override
public Page<Course> searchCourses(PageRequest domainRequest) {
    // Convert to Spring Pageable
    Pageable pageable = PageConverter.toSpringPageable(domainRequest);
    
    // Query JPA
    org.springframework.data.domain.Page<CourseEntity> jpaPage = jpaRepository.findAll(pageable);
    
    // Convert back to Domain Page
    return PageConverter.toDomainPage(jpaPage).map(mapper::toDomain);
}
```

---

### 4. 트랜잭셔널 메시징 (Outbox Pattern)

MSA 간 데이터 정합성을 위해 **"이벤트 발행"을 DB 트랜잭션에 포함**시키는 패턴입니다. `OutboxEvent` 엔티티와 리포지토리가 제공됩니다.

**🛠️ 메커니즘 (자동화됨)**

1. 도메인 로직 수행 후 `DomainEvent` 발생.
2. 이벤트 리스너가 이를 `OutboxEvent` 엔티티로 변환하여 DB에 `INSERT` (비즈니스 로직과 동일 트랜잭션).
3. 별도의 스케줄러(Poller)나 CDC(Change Data Capture)가 `outbox_events` 테이블을 읽어 실제 Kafka/RabbitMQ로 발행.

> Developer Note: 개발자는 비즈니스 로직에만 집중하면 됩니다. 인프라 계층이 OutboxEvent 저장을 처리합니다. (단, outbox_events 테이블 생성 DDL은 필수)
>

---

### 5. 재시도 전략 (Retry)

일시적인 DB 락 충돌이나 네트워크 지연 시 자동으로 재시도합니다.

- **`@Retryable`**: 메소드에 붙이면 적용됩니다. 기본적으로 낙관적 락(`OptimisticLockingFailureException`) 발생 시 재시도합니다.
- **설정:** 기본 3회 시도, 100ms 대기. (`maxAttempts`, `backoffMillis`로 커스텀 가능)

```java
@Retryable(maxAttempts = 5) // 5번까지 재시도
public void decreaseStock(Long itemId) {
    // 동시성 충돌 가능성이 높은 로직
}
```

---

### 6. CQRS 및 트랜잭션 실행기

- **`SimpleCommandBus` / `SimpleQueryBus`**: Spring Bean으로 등록된 핸들러를 자동으로 찾아 실행합니다.
- **`TransactionalUseCaseExecutor`**: UseCase 인터페이스 실행 시 트랜잭션 경계를 명시적으로 제어할 때 유용합니다. (예: `REQUIRES_NEW`가 필요한 경우)

```java
// 기존 트랜잭션과 분리하여 실행해야 하는 경우
useCaseExecutor.executeInNewTransaction(auditLogUseCase, logCommand);
```

---

## ✅ Check Point

- [ ]  Entity가 `BaseJpaEntity` 계열을 상속받아 `Auditing` 기능을 사용 중인가?
- [ ]  API 응답 시 `ResponseEntity` 대신 `ApiResponse`를 사용하고 있는가?
- [ ]  동시성 이슈가 예상되는 중요 로직에 `@Retryable`이나 `BaseVersionedJpaEntity`를 적용했는가?
- [ ]  `outbox_events` 테이블이 스키마에 포함되어 있는가?