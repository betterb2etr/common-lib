## 📘 LXP Common Application Module Guide

`common-application` 모듈은 헥사고날 아키텍처와 CQRS 패턴을 구현하기 위한 **애플리케이션 계층의 표준 인터페이스**를 제공합니다. 외부 프레임워크(Spring, JPA 등)에 대한 직접적인 의존 없이, 순수한 제어 흐름과 포트 정의만을 포함합니다.

1. 아키텍처 구성 요소 (Ports & UseCases)

비즈니스 로직의 진입점(In)과 외부 통신(Out)을 정의하는 핵심 인터페이스입니다.

| 구분 | 인터페이스명 | 용도 | 구현 위치 |
| --- | --- | --- | --- |
| **In** | `UseCase<I, O>` | 가장 기본적인 입력 포트 마커 인터페이스 | Application Service |
| **In** | `CommandUseCase<I>` | 상태 변경(CUD) 로직의 진입점. (리턴값 없음) | Command Service |
| **In** | `QueryUseCase<I, O>` | 조회(R) 로직의 진입점. | Query Service |
| **Out** | `DomainEventPublisher` | 도메인 이벤트 발행을 위한 출력 포트 | Infrastructure (Spring Event) |
| **Out** | `IntegrationEventPublisher` | 타 BC로 이벤트 전송을 위한 출력 포트 | Infrastructure (Kafka/RabbitMQ) |
| **Out** | `DomainMapper<D, E>` | Domain 모델 ↔ DB Entity 간 변환기 | Application (Mapper) |

💡 Usage Example: UseCase 구현

```java
// Service 구현 시 명확한 의도를 드러내기 위해 구체적인 UseCase 인터페이스를 구현합니다.
@Service
@RequiredArgsConstructor
public class CreateRecommendationService implements CommandUseCase<CreateRecommendationCommand> {

    private final RecommendationRepositoryPort repository;
    private final DomainEventPublisher eventPublisher;

    @Override
    public void execute(CreateRecommendationCommand command) {
        // 비즈니스 로직 수행
        MemberRecommendation recommendation = MemberRecommendation.create(...);
        repository.save(recommendation);
        
        // 도메인 이벤트 발행 (Aggregate 내부 이벤트 자동 수집 후 발행)
        eventPublisher.publishAndClear(recommendation);
    }
}
```

---

2. CQRS 패턴 (Command / Query Bus)

복잡한 서비스 레이어를 간소화하고, 명령과 조회를 분리하기 위한 버스 패턴 인터페이스입니다.

주요 인터페이스

- **Command / Query**: 데이터 전송 객체(DTO) 역할을 하는 마커 인터페이스.
- **Bus**: 핸들러에게 요청을 라우팅하는 매개체 (`dispatch`).
- **Handler**: 실제 비즈니스 로직을 수행하는 실행체 (`handle`).

### 🚀 Quick Start: Bus 패턴 적용하기

```java
**1. Command 정의**

public record CancelEnrollmentCommand(Long userId, Long courseId) implements Command {}
**2. Handler 구현**

`@Component
public class CancelEnrollmentHandler implements CommandHandler<CancelEnrollmentCommand> {
    @Override
    public void handle(CancelEnrollmentCommand command) {
        // 로직 구현
    }
}`

**3. Controller에서 사용**

`@RestController
public class EnrollmentController {
    private final CommandBus commandBus; // 생성자 주입

    @PostMapping("/cancel")
    public void cancel(@RequestBody CancelRequest request) {
        // Bus를 통해 핸들러로 위임 (Controller는 Service를 몰라도 됨)
        commandBus.dispatch(new CancelEnrollmentCommand(request.getUserId(), ...));
    }
}`
```

---

### 3. 이벤트 시스템 (Event System)

MSA 환경에서 서비스 간 결합도를 낮추기 위한 이벤트 표준입니다.

**도메인 이벤트 (Domain Event) vs 통합 이벤트 (Integration Event)**

| 구분 | 대상 | 목적 | 인터페이스 |
| --- | --- | --- | --- |
| **Domain** | 단일 BC 내부 | 동일 트랜잭션 내 로직 분리 (Side-effect 처리) | `DomainEvent`, `DomainEventHandler` |
| **Integration** | 타 BC (외부) | 시스템 간 데이터 동기화 및 비동기 통신 | `IntegrationEvent`, `IntegrationEventHandler` |

### 🛠️ 구현 가이드: 도메인 이벤트를 통합 이벤트로 전파하기

도메인 로직 완료 후, 외부 시스템에 알림을 보내야 할 때 사용합니다.

```java
@Component
public class CourseCompletedEventHandler implements DomainEventHandler<CourseCompletedEvent> {
    
    private final IntegrationEventPublisher integrationPublisher;

    @Override
    public void handle(CourseCompletedEvent event) {
        // 1. 도메인 이벤트를 통합 이벤트로 변환 (Correlation ID 유지)
        CourseCompletedIntegrationEvent integrationEvent = new CourseCompletedIntegrationEvent(
            BaseIntegrationEvent.correlationIdFrom(event), // 도메인 이벤트 ID를 추적 ID로 사용
            event.getUserId(),
            event.getCourseId()
        );

        // 2. 외부 메시지 큐로 발행
        integrationPublisher.publish("topic.course.completed", integrationEvent);
    }

    @Override
    public Class<CourseCompletedEvent> supportedEventType() {
        return CourseCompletedEvent.class;
    }
}
```

---

### 4. 재시도 및 안정성 (Retry)

일시적인 장애 상황을 복구하기 위한 표준 어노테이션 및 정책 인터페이스입니다.

- **`@Retryable`**: 메소드 레벨에서 재시도 정책을 선언적으로 적용합니다. (AOP 설정 필요)
- **`RetryPolicy`**: 커스텀 재시도 로직이 필요할 때 구현합니다.

```java
// 예: 외부 API 호출이 실패하면 최대 3번, 500ms 간격으로 재시도
@Retryable(maxAttempts = 3, backoffMillis = 500, retryFor = {TimeoutException.class})
public ExternalCourseInfo getCourseMeta(Long courseId) {
    return externalApiClient.fetch(courseId);
}
```

✅ Check Point

- [ ]  Service 클래스는 `UseCase` 인터페이스를 구현하고 있는가?
- [ ]  DB Entity 변환 시 수동 매핑 대신 `DomainMapper`를 활용하고 있는가?
- [ ]  외부 시스템 연동 시 `IntegrationEvent` 상속을 통해 `eventId`, `occurredAt` 등 표준 메타데이터를 준수하는가?
- [ ]  Aggregate의 상태 변경 후 `DomainEventPublisher.publishAndClear(aggregate)`를 호출하여 이벤트를 누락 없이 발행했는가?