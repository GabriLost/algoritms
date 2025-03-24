package reactor;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.stream.IntStream;


@Slf4j
public class ConcatSeqMap {

    @Test
    //1150ms fast parallel pretty random order
    //    res  n ms
    //    Done 0 130
    //    Done 2 146
    //    Done 4 146
    //    Done 6 146
    //    Done 8 147
    //    Done 1 1155
    //    Done 3 1156
    //    Done 5 1156
    //    Done 7 1156
    //    Done 9 1156
    public void test_flatMap() {
        var startTime = System.currentTimeMillis();
        Flux.range(0, 10)
                .publishOn(Schedulers.boundedElastic())
                .flatMap(this::doSomethingAsync)
                .doOnNext(n -> log.info("Done {} {}", n, System.currentTimeMillis() - startTime))
                .blockLast();
    }

    @Test
    //1175 ms sequential and blocking at fact, waits all, so it is less fast, but still parallel
    //    res  n ms
    //    Done 0 139
    //    Done 1 1161
    //    Done 2 1161
    //    Done 3 1161
    //    Done 4 1161
    //    Done 5 1161
    //    Done 6 1162
    //    Done 7 1162
    //    Done 8 1162
    //    Done 9 1162
    public void test_FlatMapSequential() {
        var startTime = System.currentTimeMillis();
        Flux.range(0, 10)
                .flatMapSequential(this::doSomethingAsync)
                .doOnNext(n -> log.info("Done Sequential {} {}", n, System.currentTimeMillis() - startTime))
                .blockLast();
    }


    @Test
    //5200ms slow, like iterator, blocks, not feels async
    //    res  n ms
    //    Done 0 137
    //    Done 1 1156
    //    Done 2 1156
    //    Done 3 2172
    //    Done 4 2172
    //    Done 5 3181
    //    Done 6 3181
    //    Done 7 4190
    //    Done 8 4190
    //    Done 9 5195
    public void test_flatMap_flatMapSequential_concatMap() {
        var startTime = System.currentTimeMillis();
        Flux.range(0, 10)
                .concatMap(this::doSomethingAsync)
                .doOnNext(n -> log.info("Done concatMap {} {}", n, System.currentTimeMillis() - startTime))
                .blockLast();
    }

    private Mono<Integer> doSomethingAsync(Integer number) {
        //add some delay for the odd item...
        return (number & 1) == 1 ?
                Mono.just(number).delayElement(Duration.ofSeconds(1)) :
                Mono.just(number);
    }

    @Test
    public void testOddOrEven() {
        IntStream.range(1, 10)
                .forEach(value -> log.info("number {} is {}", value, (value & 1) == 1 ? "odd" : "even"));
    }

}
