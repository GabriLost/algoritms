package interview;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.IntStream;

public class WarmUpQuestions {

    @Test
    public void streamTest() {

        var findFirst = IntStream
                .range(1, 10000).boxed()
                .findFirst();

        var findAny = IntStream
                .range(1, 10000).boxed()
                .findAny();

        System.out.println("findFirst result: " + findFirst.get());
        System.out.println("findAny result  : " + findAny.get());

    }

    @Test
    public void hashSetTest() {
        Set<String> leaders = new HashSet<>(Arrays.asList("Сталин", "Ленин", "Че Гевара"));
        System.out.println("Leaders = " + leaders);
    }


    @Test
    public void intStreamTest() {
        var res = IntStream.iterate(1, x -> x + 1)
                .peek(System.out::println)
                .sorted()
                .limit(5)
                .findFirst();

        System.out.println(res);
    }

    @Test
    public void listRemove1() {
        var list = new ArrayList<>(Arrays.asList("A", "B", "C"));
        list.stream().forEach(x -> {
            if (x.equals("C")) {
                list.remove(x);
            }
        });
    }

    @Test
    public void listRemove2() {
        var list = new ArrayList<>(Arrays.asList("A", "B", "C"));
        list.stream().forEach(x -> {
            if (x.equals("A")) {
                list.remove(x);
            }
        });
    }
}
