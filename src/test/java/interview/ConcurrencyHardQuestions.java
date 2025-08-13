package interview;

import interview.locks.AtomicIntegerExample;
import interview.locks.ReadWriteLockExample;
import interview.locks.ReentrantLockExample;
import interview.locks.SynchronizedExample;
import org.junit.Test;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.util.concurrent.ForkJoinPool;

public class ConcurrencyHardQuestions {

    /**
     * https://www.geeksforgeeks.org/lock-framework-vs-thread-synchronization-in-java/
     * https://winterbe.com/posts/2015/04/30/java8-concurrency-tutorial-synchronized-locks-examples/
     * https://www.baeldung.com/java-concurrent-locks
     * https://jenkov.com/tutorials/java-concurrency/read-write-locks.html
     */
    @Test
    public void synchronizedVsLocksTest() {
        ReentrantLockExample lockExample = new ReentrantLockExample();
        SynchronizedExample synchronizedExample = new SynchronizedExample();
        AtomicIntegerExample atomicIntegerExample = new AtomicIntegerExample();
        ReadWriteLockExample readWriteLockExample = new ReadWriteLockExample();

        try (ForkJoinPool customThreadPool = new ForkJoinPool(4)) {
            System.out.println("lock =" + lockExample.getCount());
            for (int i = 0; i < 10; i++) {
                customThreadPool.submit(() -> {
                    lockExample.increment();
                    System.out.println("lock =" + lockExample.getCount());
                    synchronizedExample.increment();
                    System.out.println("synchronized =" + synchronizedExample.getCount());
                    atomicIntegerExample.increment();
                    System.out.println("atomic =" + atomicIntegerExample.getCount());
                    readWriteLockExample.increment();
                    System.out.println("readWrite =" + readWriteLockExample.getCount());
                });
            }

            customThreadPool.shutdown();
        }
    }

    @RepeatedTest(100)
    @Execution(ExecutionMode.CONCURRENT)
    void happensBeforeForFinal() throws InterruptedException {
        final HappensBeforeAndFinalTest[] var = {null};
        var checkThread = new Thread(() -> {
            if (var[0] != null) {
                assert var[0].nonFinalVar == 1;
                assert var[0].finalVar == 1;
            }
        });
        checkThread.start();

        new Thread(() -> var[0] = new HappensBeforeAndFinalTest(1, 1)).start();

        checkThread.join();
    }

    public static class HappensBeforeAndFinalTest {
        public final int finalVar;
        public int nonFinalVar = -1;

        public HappensBeforeAndFinalTest(int finalVar, int nonFinalVar) {
            this.finalVar = finalVar;
            this.nonFinalVar = nonFinalVar;
        }
    }
}
