package leetcode;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class _009_PalindromeNumber {

    @Test
    public void test_Solution() {
        var s = new Solution();

        assertEquals(false, s.isPalindrome(1000021));
        assertEquals(true, s.isPalindrome(121));
        assertEquals(true, s.isPalindrome(0));
        assertEquals(false, s.isPalindrome(-121));
        assertEquals(false, s.isPalindrome(10));
    }

}

class Solution {
    public boolean isPalindrome(int x) {
        int length = (int) (Math.log10(x) + 1);
        if (x < 0)
            return false;
        if (x < 10)
            return true;

        var rank = (int) Math.pow(10, length - 1);
        var front = x / rank;
        var back = (int) x % 10;
        if (front != back) {
            return false;
        }
        x = x - (int) front * rank;
        x = x % 10;

        return isPalindrome(x);
    }
}