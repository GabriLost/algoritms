package interview;

import java.util.concurrent.CountDownLatch;

public class CountDownLatchDemo {

    private static int count;

    public static void main(String[] args) throws InterruptedException {

        var latch = new CountDownLatch(100);

        for (int i = 0; i < 100; i++) {
            new Thread(() -> {
                for (int k = 0; k < 1000; k++) {
                    count++;
                }

            }).start();

            latch.countDown();
        }

        latch.await();

        System.out.println(count);
    }
}