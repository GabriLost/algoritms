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

    @Test
    public void fluxSequence()  {

        var startTime = System.currentTimeMillis();
        Flux.range(0, 5)
                .flatMap(this::longLivedFunction)
                .flatMap(this::longLivedFunction)
                .doOnNext(integer -> longLivedFunction(integer).subscribe())
                .doFinally(type -> System.out.printf("Done %s  \n", System.currentTimeMillis() - startTime))
                .blockLast();
    }

    @Test
    public void fluxCombineSubFlatMap() {

        var startTime = System.currentTimeMillis();
        Flux.range(0, 5)
                .flatMap(i -> longLivedFunction(i)
                        .flatMap(j -> longLivedFunction(j)))
                .doOnNext(i -> System.out.println("this is i " + i))
                .doFinally(type -> System.out.printf("Done %s \n", System.currentTimeMillis() - startTime))
                .blockLast();

    }

    @Test
    public void fluxCombine() {

        var startTime = System.currentTimeMillis();
        Flux.range(0, 5)
                .flatMap(i -> Flux.combineLatest(longLivedFunction(i), longLivedFunction(i), (integer, integer2) -> integer))
                .doOnNext(i -> System.out.println("this is i " + i))
                .doFinally(type -> System.out.printf("Done %s \n", System.currentTimeMillis() - startTime))
                .blockLast();

    }


    private Mono<Integer> longLivedFunction(Integer number) {
        System.out.println("HI " + number);
        return Mono.just(number)
                .doOnNext(integer -> System.out.println("longLivedFunction start " + integer))
                .delayElement(Duration.ofSeconds(1))
                .doOnNext(integer -> System.out.println("longLivedFunction end " + integer));
    }
}
