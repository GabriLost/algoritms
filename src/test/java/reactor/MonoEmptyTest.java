package reactor;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class MonoEmptyTest {

    @Test
    public void understandingHowEmptyWorks() {

        var res = Flux.range(0, 5)
                .flatMap(i -> {
                    if (i % 2 == 0) {
                        return Mono.empty();
                    } else return Mono.just(i);
                })
                .doOnNext(n -> System.out.printf("Done %s \n", n))
                .collectList()
                .block();

        assertEquals(res, List.of(1, 3));

    }

}
