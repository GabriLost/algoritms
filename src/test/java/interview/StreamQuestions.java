package interview;

import org.junit.Test;

import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.IntStream;

public class StreamQuestions {

    @Test
    public void findFirstFindAnyDifference() {

        var findFirst = IntStream
                .range(1, 100).boxed()
                .findFirst();

        var findAny = IntStream
                .range(1, 100).boxed()
                .parallel()
                .findFirst();

        System.out.println("findFirst result: " + findFirst.get());
        System.out.println("findAny result  : " + findAny.get());

    }

    @Test
    public void parallelStreamBasic() {
        var numbers = Arrays.asList(1, 3, 2, 5, 4, 6, 7, 8, 10, 9);

        numbers.stream()
                .parallel()
                .sorted()
                .forEach(System.out::println);
    }

    @Test
    public void sortedStreamTest() {
        var res = IntStream.iterate(1, x -> x + 1)
//                .sorted()
                .limit(5)
                .max();

        System.out.println(res);
    }

    @Test
    public void parallelUndefinedBehaviour() {
        var numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        numbers.parallelStream()
                .filter(n -> n % 2 == 0)
                // potentially failed call
                .map(n -> getObjectSometimesWithException(n))
                .forEach(System.out::println);
    }

    private static String getObjectSometimesWithException(int n) {
        if (n % 4 == 0) {
            throw new RuntimeException("oh no");
        } else {
            return "some object " + n;
        }
    }

    /**
     * https://www.baeldung.com/java-8-parallel-streams-custom-threadpool
     * https://medium.com/geekculture/pitfalls-of-java-parallel-streams-731fe0c1eb5f
     */
    @Test
    public void parallelStreamWithSpecifiedPool() {
        var numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        ForkJoinPool customThreadPool = new ForkJoinPool(4);

        customThreadPool.submit(() -> numbers.parallelStream()
                .filter(n -> n % 2 == 0)
                .map(n -> n * n)
                .forEach(System.out::println)).join();

        customThreadPool.shutdown();
    }
}
