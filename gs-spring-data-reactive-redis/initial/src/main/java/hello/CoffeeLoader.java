package hello;

import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.http.converter.json.GsonBuilderUtils;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import javax.annotation.PostConstruct;
import java.util.UUID;

@Component
public class CoffeeLoader {

    private final ReactiveRedisConnectionFactory factory;
    private final ReactiveRedisOperations<String, Coffee> operations;

    public CoffeeLoader(ReactiveRedisConnectionFactory factory, ReactiveRedisOperations<String, Coffee> operations) {
        this.factory = factory;
        this.operations = operations;
    }

    @PostConstruct
    public void loadData() {
        factory
                .getReactiveConnection()
                .serverCommands()
                .flushAll()
                .thenMany(Flux
                        .just("Jet Black Redis", "Darth Redis", "Black Alert Redis")
                        .map(name -> new Coffee(UUID.randomUUID().toString(), name))
                        .flatMap(coffee -> operations.opsForValue().set(coffee.getId(), coffee)))
                .thenMany(operations.keys("*")).flatMap(operations.opsForValue()::get)
                .subscribe(System.out::println);
    }
}
