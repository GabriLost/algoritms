package leetcode;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class _0011_ContainerWithMostWater {

    @Test
    public void testSolution() {
        var s = new Solution();
        assertEquals(49, s.maxArea(new int[] { 1, 8, 6, 2, 5, 4, 8, 3, 7 }));
        assertEquals(1, s.maxArea(new int[] { 1, 1 }));
    }

    class Solution {
        public int maxArea(int[] height) {
            var max = 0;
            var left = 0;
            var right = height.length - 1;

            while (left < right) {
                max = Math.max(max, (right - left) * Math.min(height[left], height[right]));
                if (height[right] > height[left])
                    left++;
                else
                    right--;
            }

            return max;
        }
    }
}