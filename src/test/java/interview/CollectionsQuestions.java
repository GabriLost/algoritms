package interview;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class CollectionsQuestions {

    @Test
    public void hashSetTest() {
        Set<String> alphabet = new HashSet<>(Arrays.asList("A", "B", "C", "Hello there", "alphabet"));
        System.out.println("alphabet = " + alphabet);
    }


    @Test
    public void listRemove1() {
        var list = new ArrayList<>(Arrays.asList("A", "B", "C"));
        list.stream().forEach(x -> {
            if (x.equals("C")) {
                list.remove(x);
            }
        });
        System.out.println(list);
    }

    @Test
    public void listRemove2() {
        var list = new ArrayList<>(Arrays.asList("A", "B", "C"));
        list.stream().forEach(x -> {
            if (x.equals("A")) {
                list.remove(x);
            }
        });
        System.out.println(list);
    }
}
