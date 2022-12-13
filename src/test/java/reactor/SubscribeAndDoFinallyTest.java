package reactor;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

public class SubscribeAndDoFinallyTest {

    @Test
    public void understandingHowDoFinallyWorks() throws InterruptedException {

        Flux.range(0, 5)
                .doOnNext(i -> longLivedFunction(i).subscribe())
                .doOnNext(n -> System.out.printf("Done %s \n", n))
                .doFinally(type -> System.out.println("doFinally with signal: " + type))
                .blockLast();

        Thread.sleep(2000);
    }


    private Mono<Integer> longLivedFunction(Integer number) {
        return Mono.just(number)
                .doOnNext(integer -> System.out.println("longLivedFunction start " + integer))
                .delayElement(Duration.ofSeconds(1))
                .doOnNext(integer -> System.out.println("longLivedFunction end " + integer));
    }
}
