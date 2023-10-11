package reactor;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.util.function.Tuples;

import java.util.ArrayList;
import java.util.stream.IntStream;

import static org.junit.Assert.assertTrue;


@Slf4j
public class FluentTimingTest {

    @Test
    public void long_fluent_to_times_slower_then_short_fluent() {
        var res = IntStream.range(0, 1_000).boxed()// с 1, 10, 1000, 10_000 одинаковый результат
                .map(i -> Tuples.of(long_fluent_timing(), short_fluent_timing()))
                .mapToDouble(value -> (double) value.getT1() / value.getT2())
                .average()
                .orElse(Double.NaN);


        log.info("реактивная цепочка работает дольше в {} раз", res);
        assertTrue(res > 1);
    }


    private long short_fluent_timing() {
        var started = System.currentTimeMillis();
        var i10List = new ArrayList<Integer>();
        var i100List = new ArrayList<Integer>();

        Flux.range(1, 100_000)
                .handle((i, s) -> {
                            var ix10 = i * 10;
                            if (ix10 % 10 != 0) {
                                return;
                            }

                            log.debug("ix10:" + i * 10);
                            i10List.add(1);
                            var ix100 = ix10 * 10;
                            if (ix100 * 100 != 0) {
                                return;
                            }
                            log.debug("ix100:" + ix100);
                            i100List.add(1);
                            var ix200 = i * 100 * 2;
                            if (ix200 > 1_000_000_000) {
                                log.debug("i > 1_000_000_000:" + 1);
                                s.next(ix200);
                            }
                        }
                )
                .blockLast();
        return System.currentTimeMillis() - started;
    }

    private long long_fluent_timing() {
        var started = System.currentTimeMillis();
        var i10List = new ArrayList<Integer>();
        var i100List = new ArrayList<Integer>();

        Flux.range(1, 100_000)
                .map(i -> i * 10)
                .doOnNext(i -> {
                    log.debug("1x10:" + i);
                    i10List.add(i);
                })
                .filter(i -> i % 10 == 0)
                .map(i -> i * 10)
                .filter(i -> i % 100 == 0)
                .doOnNext(i -> {
                    log.debug("ix100:" + i);
                    i100List.add(i);
                })
                .map(i -> i * 2)
                .filter(i -> i > 1_000_000_000)
                .doOnNext(i -> log.debug("i > 1_000_000_000:" + i))
                .blockLast();

        return System.currentTimeMillis() - started;
    }
}