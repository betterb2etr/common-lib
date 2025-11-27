

## Command 정의
```java

public record CreateOrderCommand(
    Long customerId,
    List<OrderItemRequest> items,
    String shippingAddress
) implements Command {
}

```

## Commnand Handler 구현
```java
@Component
public class CreateOrderCommandHandler implements CommandHandler<CreateOrderCommand> {

    private final OrderRepository orderRepository;
    private final DomainEventPublisher eventPublisher;

    @Override
    public void handle(CreateOrderCommand command) {
        Order order = Order.create(
            command.customerId(),
            command.items(),
            command.shippingAddress()
        );
        
        orderRepository.save(order);
        eventPublisher.publishAndClear(order);
    }
}
```


## 직접 usecase 를 사용한 commandBus 사용
```java
@Component
public class OrderCommandBus implements CommandBus {

    private final Map<Class<?>, Consumer<? extends Command>> handlers = new HashMap<>();

    public SimpleCommandBus(EnrollmentService service) {
        handlers.put(CreateOrderCommand.class, (Consumer<CreateOrderCommand>) service::order);
        handlers.put(CancelOrderommand.class, (Consumer<CancelOrderCommand>) service::cancel);
    }

    @Override
    public <C extends Command> void dispatch(C command) {
        Consumer<C> handler = (Consumer<C>) handlers.get(command.getClass());
        handler.accept(command);
    }
}
```


## Controller에서 CommandBus 사용
```java
@RestController
@RequestMapping("/orders")
public class OrderController {

    private final CommandBus commandBus;

    @PostMapping
    public ResponseEntity<Void> createOrder(@RequestBody CreateOrderRequest request) {
        CreateOrderCommand command = new CreateOrderCommand(
            request.customerId(),
            request.items(),
            request.shippingAddress()
        );
        
        commandBus.dispatch(command);
        
        return ResponseEntity.ok().build();
    }
}
```