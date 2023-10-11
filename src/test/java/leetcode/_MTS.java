package leetcode;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class _MTS {

    /**
     * Задача:
     * Нужно декодировать строку.
     * Правило кодирования: k[encoded_string] , где encoded_string  внутри квадратных скобок повторяется ровно k раз. k > 0 && k <= 100
     * Входная строка всегда валидна
     * Пример 1:
     * Ввод: s = "3[a]2[bc]"
     * Вывод: "aaabcbc"
     * Пример 2 (вложенный)
     * Ввод: s = "3[a2[c]]"
     * Вывод: "accaccacc"
     */
    @Test
    public void test_Solution() {

        var test = "3[a]2[bc]2[qwe]";
        var actual = "aaabcbcqweqwe";
        var res = solution(test);


        assertEquals(actual, res);

    }

    public String solution(String s) {
        while (s.contains("[")) {
            s = resolve(s);
        }
        return s;
    }

    public String resolve(String s) {
        var left = s.lastIndexOf("[");
        var right = s.lastIndexOf("]");
        var second_right = s.substring(0, left).lastIndexOf("]");

        var n = Integer.parseInt(s.substring(second_right + 1, left));

        StringBuilder phrase = new StringBuilder(s.substring(left + 1, right));
        phrase.append(String.valueOf(phrase).repeat(Math.max(0, n - 1)));

        return s.substring(0, s.lastIndexOf(String.valueOf(n))) + phrase + s.substring(right + 1);
    }

}
