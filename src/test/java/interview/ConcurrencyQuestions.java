package interview;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.parallel.Execution;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.parallel.ExecutionMode.CONCURRENT;

class ConcurrencyQuestions {

    @Execution(CONCURRENT)
    @RepeatedTest(10)
    void volatileTests() throws InterruptedException {
        var volatileExample = new VolatileExample();

        volatileExample.doTheWorkInParallel();
    }

    /**
     * <a href="https://medium.com/@basecs101/difference-between-atomic-volatile-and-synchronized-in-java-programming-14156c3c8f46">difference-between-atomic-volatile-and-synchronized-in-java-programming</a>
     * <br>
     * <a href="https://www.baeldung.com/java-wait-notify">wait-notify</a>
     * <br>
     * <a href="https://web.archive.org/web/20220628134440/https://habr.com/ru/post/143237/">А как же всё-таки работает многопоточность? Часть I: синхронизация</a>
     */
    @Execution(CONCURRENT)
    @RepeatedTest(10)
    public void volatilePlusPlusTests() throws InterruptedException {
        var volatileExample = new VolatileExample();

        volatileExample.doTheWorkInParallelVolatile();
    }

    /**
     * https://www.baeldung.com/java-volatile
     * https://www.javatpoint.com/volatile-keyword-in-java
     * https://habr.com/ru/post/685518/
     * https://habr.com/ru/company/golovachcourses/blog/221133/
     * https://web.archive.org/web/20220519121335/https://habr.com/en/post/133981/
     */
    @Execution(CONCURRENT)
    @RepeatedTest(10)
    public void realVolatileTests() {
        var volatileExample = new VolatileExample();

        volatileExample.realVolatileUsage();
    }


    /**
     * https://www.baeldung.com/lock-free-programming
     * https://habr.com/ru/post/319036/
     * https://www.youtube.com/watch?v=XivoUctdPIU
     */
    @Execution(CONCURRENT)
    @RepeatedTest(10)
    public void atomicTests() throws InterruptedException {
        var volatileExample = new VolatileExample();

        volatileExample.doTheWorkInParallelAtomicInteger();
    }

    /**
     * https://www.youtube.com/watch?v=kwS3OeoVCno&ab_channel=JPoint,Joker%D0%B8JUGru
     * https://www.geeksforgeeks.org/difference-between-java-threads-and-os-threads/
     * https://medium.com/@unmeshvjoshi/how-java-thread-maps-to-os-thread-e280a9fb2e06
     * https://www.baeldung.com/java-start-thread
     */
    @Execution(CONCURRENT)
    @RepeatedTest(10)
    void startThreadTests() {
        // 1
        new Thread() {
            @Override
            public void run() {
                super.run();
                System.out.println(1);
            }
        }.start();

        // 2
        new Thread(() -> System.out.println(2)).start();

        // 3
        ExecutorService executor = Executors.newFixedThreadPool(10);
        executor.execute(() -> System.out.println(3));

        // 4
        CompletableFuture.runAsync(() -> System.out.println(4));
    }


    public static class VolatileExample {
        private int value = 0;

        private volatile int volatileValue = 0;

        private final AtomicInteger atomicInteger = new AtomicInteger(0);


        public void doTheWorkInParallel() throws InterruptedException {
            int repeatTime = 1000;
            int numberOfThreads = 100;
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < repeatTime; i++) {
                        value++;
                    }
                }
            };

            Thread[] workers = new Thread[numberOfThreads];
            for (int i = 0; i < numberOfThreads; i++) {
                workers[i] = new Thread(runnable, String.valueOf(i));
                workers[i].start();
            }
            for (int j = 0; j < numberOfThreads; j++) {
                workers[j].join(); //todo add catch exception
            }

            System.out.printf("value = %d expected = %d\n", value, repeatTime * numberOfThreads);
            assert value == repeatTime * numberOfThreads;
        }

        public void doTheWorkInParallelVolatile() throws InterruptedException {
            int repeatTime = 1000;
            int numberOfThreads = 1000;
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < repeatTime; i++) {
                        volatileValue++;
                    }
                }
            };

            Thread workers[] = new Thread[numberOfThreads];
            for (int i = 0; i < numberOfThreads; i++) {
                workers[i] = new Thread(runnable, String.valueOf(i));
                workers[i].start();
            }
            for (int j = 0; j < numberOfThreads; j++) {
                workers[j].join(); //todo add catch exception
            }

            System.out.printf("value = %d expected = %d", volatileValue, repeatTime * numberOfThreads);
            assert volatileValue == repeatTime * numberOfThreads;
        }

        public void doTheWorkInParallelAtomicInteger() throws InterruptedException {
            int repeatTime = 1000;
            int numberOfThreads = 1000;
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < repeatTime; i++) {
                        atomicInteger.incrementAndGet();
                    }
                }
            };

            Thread workers[] = new Thread[numberOfThreads];
            for (int i = 0; i < numberOfThreads; i++) {
                workers[i] = new Thread(runnable, String.valueOf(i));
                workers[i].start();
            }
            for (int j = 0; j < numberOfThreads; j++) {
                workers[j].join(); //todo add catch exception
            }

            System.out.printf("value = %d expected = %d", atomicInteger.get(), repeatTime * numberOfThreads);
            assert atomicInteger.get() == repeatTime * numberOfThreads;
        }

        public void realVolatileUsage() {

            new Thread(new Runnable() {
                @Override
                public void run() {
                    value = 1;
                    volatileValue = 1;
                }
            }).start();

            // copy values, because on else other thread may apply values
            int valueTemp = value;
            int volatileValueTemp = volatileValue;

            if (volatileValueTemp == 1) {
                assert valueTemp == 1;
            } else {
                assert valueTemp == 0;
            }
        }
    }



}