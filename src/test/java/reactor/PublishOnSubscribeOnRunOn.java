package reactor;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.time.Instant;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Slf4j
public class PublishOnSubscribeOnRunOn {


    @Test
    public void subscribeOnMethodTest() {
//        Scheduler s = Schedulers.newSingle("s");
        Scheduler s = Schedulers.newParallel("p", 4);
//        Scheduler s = Schedulers.newBoundedElastic(10, 100, "gay", 60, false);
        var count = 40;

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


    @Test
    public void publishOnTest() throws InterruptedException {
        Scheduler s = Schedulers.newParallel("parallel-scheduler", 4);

        final Flux<String> flux = Flux
                .range(1, 2)
                .map(i -> {
                    System.out.println(Thread.currentThread().getName());
                    return 10 + i;
                })
                .publishOn(s)
                .map(i -> {
                    System.out.println(Thread.currentThread().getName());
                    return "value " + i;
                });

        var t = new Thread(() -> flux.subscribe(System.out::println));
        t.start();
        t.join();
    }

    @Test
    public void subscribeOnTest() throws InterruptedException {
        Scheduler s = Schedulers.newParallel("parallel-scheduler", 4);

        final Flux<String> flux = Flux
                .range(1, 2)
                .subscribeOn(s)
                .map(i -> {
                    System.out.println(Thread.currentThread().getName());
                    return 10 + i;
                })

                .map(i -> {
                    System.out.println(Thread.currentThread().getName());
                    return "value " + i;
                });

        var t = new Thread(() -> flux.subscribe(System.out::println));

        t.start();
        t.join();
    }


    @Test
    public void test9() {
        var res = IntStream.of(0, 1, 2)
                .map(i -> i + 1)
                .reduce(1, (a, s) -> a + s);

        Stream<Integer> val = IntStream.rangeClosed(10, 15).boxed();
        Object obj = val.collect(Collectors.partitioningBy(x -> x % 2 == 0));

        System.out.println(obj);

    }

    @Test
    public void test10() {
        Stream<Integer> val = IntStream.rangeClosed(10, 15).boxed();
        Object obj = val.collect(Collectors.partitioningBy(x -> x % 2 == 0));
        System.out.println(obj);
        //{false=[11, 13, 15], true=[10, 12, 14]}
    }

    //11 ::println

    @Test
    public void test12() {
        int[] arr = {1, 2, 3, 4};
//        Predicate<Integer> filter = val -> val % 2 == 0;
        Predicate<Integer> filter = (Integer val) ->
                val % 2 == 0;
        for (int i = 0; i < arr.length; i++) {
            int current = arr[i];
            if (filter.test(current)) {
                System.out.println(current);
            }
        }
    }

    @Test
    public void test17() {
        int i = 0;
        while (i < 5) {
            for (int j = 0; j < 30; j += 10) {
                if (i == 2) {
                    continue;
                } else {
                    break;
                }
            }
            System.out.print(i++);
            //01234
        }
    }

    @Test
    public void test18() {
        FavoriteBook[] favoriteBooks = new FavoriteBook[3];
        Book[] books = favoriteBooks;
        books[1] = new Book();
        System.out.println("OK!");
    }

    static class Book {
    }

    static class FavoriteBook extends Book {
    }

    @Test
    public void test19() {
        var book1 = new Book() {
        };
        var book2 = new Book() {
        };
        System.out.println(compareClasses(book1, book1));
        System.out.println(compareClasses(book1, book2));
        System.out.println(compareClasses(book1, getConcrete()));
        System.out.println(compareClasses(getConcrete(), getConcrete()));
        //b3, b5
    }

    public static Book getConcrete() {
        return new Book() {
        };
    }


    public static boolean compareClasses(Object o1, Object o2) {
        return o1.getClass() == o2.getClass();
    }

//    @Test
//    public void test24() {
//        Instant.class;
//    }


}
