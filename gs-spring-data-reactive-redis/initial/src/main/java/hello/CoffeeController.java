package hello;

import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class CoffeeController {

    private final ReactiveRedisOperations<String, Coffee> operations;

    public CoffeeController(ReactiveRedisOperations<String, Coffee> operations) {
        this.operations = operations;
    }

    @GetMapping("/coffees")
    public Flux<Coffee> all() {
        return operations
                .keys("*")
                .flatMap(operations.opsForValue()::get);
    }
}
