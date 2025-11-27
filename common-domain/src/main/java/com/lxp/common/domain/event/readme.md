correlationId (상관관계 ID)
하나의 요청이 여러 서비스를 거칠 때 전체 흐름을 추적하는 ID
```aiignore
사용자 주문 요청 (correlationId: "abc-123")
│
├─→ Order Service (correlationId: "abc-123")
│       └─→ OrderCreatedEvent
│
├─→ Payment Service (correlationId: "abc-123")
│       └─→ PaymentCompletedEvent
│
└─→ Shipping Service (correlationId: "abc-123")
└─→ ShippingStartedEvent

```

causationId (원인 ID)
이 이벤트를 발생시킨 직전 이벤트의 ID

```aiignore
causationId (원인 ID)
이 이벤트를 발생시킨 직전 이벤트의 ID
OrderCreatedEvent (eventId: "event-1", causationId: null)
│
└─→ PaymentCompletedEvent (eventId: "event-2", causationId: "event-1")
│
└─→ ShippingStartedEvent (eventId: "event-3", causationId: "event-2")
인과관계 체인을 추적할 수 있어. "이 이벤트가 왜 발생했지?" → causationId 따라가면 됨.
```
