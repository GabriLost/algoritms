package leetcode;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class _009_PalindromeNumber {

    @Test
    public void test_Solution() {

        Solution s = new CharSolution();

        assertEquals(false, s.isPalindrome(1000021));
        assertEquals(true, s.isPalindrome(121));
        assertEquals(true, s.isPalindrome(0));
        assertEquals(false, s.isPalindrome(-121));
        assertEquals(false, s.isPalindrome(10));

        s = new IntSolution();

        assertEquals(false, s.isPalindrome(1000021));
        assertEquals(true, s.isPalindrome(121));
        assertEquals(true, s.isPalindrome(0));
        assertEquals(false, s.isPalindrome(-121));
        assertEquals(false, s.isPalindrome(10));
    }

}

interface Solution {
    boolean isPalindrome(int x);
}

// hard 1 hour to solve
class IntSolution implements Solution {
    public boolean isPalindrome(int x) {
        if (x % 10 == 0 && x != 0)
            return false;

        var pointer = 0;

        // make reverse of x, but we need only half digits
        while (pointer < x) {
            pointer = pointer * 10 + x % 10;
            x = x / 10;
        }

        return pointer == x || pointer / 10 == x;
    }
}

// easy 1 min to solve
class CharSolution implements Solution {
    public boolean isPalindrome(int x) {

        var chars = String.valueOf(x).toCharArray();
        var length = chars.length;

        for (var i = 0; i < length / 2; i++) {
            if (chars[i] != chars[length - 1]) {
                return false;
            }
        }

        return true;
    }
}