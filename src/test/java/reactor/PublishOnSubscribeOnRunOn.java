package reactor;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.time.Duration;

@Slf4j
public class PublishOnSubscribeOnRunOn {


    @Test
    public void subscribeOnMethodTest() {
//        Scheduler s = Schedulers.newSingle("s");
        Scheduler s = Schedulers.newParallel("p", 4);
//        Scheduler s = Schedulers.newBoundedElastic(10, 100, "gay", 60, false);
        var count = 4;

        StepVerifier.create(Flux
                                .range(1, count)
                                .publishOn(s)
//                                .parallel(2)
//                                .runOn(s)
//                        .publishOn(s)
                                .flatMap(this::doSomethingBlockingWrap)
//                                .flatMap(this::doSomethingBlockingWrapMono)
                )
                .expectNextCount(count)
                .verifyComplete();
    }


    private Mono<Integer> doSomethingAsync(Integer number) {
        return Mono.just(number)
                .delayElement(Duration.ofSeconds(1))
                .doOnNext(integer -> log.info("{} {}", integer, Thread.currentThread().getName()));
    }

    private Integer doSomethingBlocking(Integer number) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        log.info("{} {}", number, Thread.currentThread().getName());
        return number;
    }

    private Mono<Integer> doSomethingBlockingWrapMono(Integer number) {
        return Mono.just(doSomethingBlocking(number));
    }

    private Mono<Integer> doSomethingBlockingWrap(Integer number) {
        Mono<Integer> blockingWrapper = Mono.fromCallable(() -> doSomethingBlocking(number));
        blockingWrapper = blockingWrapper.subscribeOn(Schedulers.boundedElastic());
        return blockingWrapper;

    }
}
