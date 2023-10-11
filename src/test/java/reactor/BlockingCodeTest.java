package reactor;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.function.Function;

@Slf4j
public class BlockingCodeTest {

    // в чем проблема блокирующего кода - он может занять все потоки
    // и даже неблокирующий код не может начать свою работу
    // пример из реальной жизни - готовка еды, ты же не стоишь у плиты и не ждешь, пока вода вскипит,
    // можешь делать что-то еще

    //BlackHound

    @Test
    public void testBlockingCode() {
        System.out.printf("processors %s\n", Runtime.getRuntime().availableProcessors());
        var startTime = System.currentTimeMillis();
// спойлер
//        Flux.range(0, Runtime.getRuntime().availableProcessors() )
        Flux.range(0, 1000)
                .subscribeOn(Schedulers.parallel())
                .map(i -> {
                    var latch = new CountDownLatch(1);//защелка
                    System.out.printf("%s\n", i);

                    Mono.delay(Duration.ofMillis(100))
                            .subscribe(___ -> latch.countDown());
                    try {
                        latch.await();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                    return i;
                })
                .doFinally(signalType -> System.out.printf("Done %s \n", System.currentTimeMillis() - startTime))
                .blockLast();


    }

    @Test
    public void freeze() {
        var count = 1000;
        StepVerifier.create(
                        Flux.range(0, count)
                                .groupBy(Function.identity())
                                .flatMap(Function.identity())
                                .doOnNext(i -> log.info("i: " + i))
                                .filter(i -> i == count))
                .verifyComplete();
    }

}
