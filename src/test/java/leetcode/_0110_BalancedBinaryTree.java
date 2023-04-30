
package leetcode;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class _0110_BalancedBinaryTree {

    @Test
    public void testSolution() {

        Integer[] arr1 = {3,9,20,null,null,15,7};
        Integer[] arr2 = {1,2,2,3,3,null,null,4,4};

        assertTrue(Solution.isBalanced(new TreeNode(arr1)));
        assertFalse(Solution.isBalanced(new TreeNode(arr2)));

    }

    static class Solution {
        public static boolean isBalanced(TreeNode root) {
            if (root == null) {
                return true;
            }
            return height(root) > 0;
        }

        private static int height(TreeNode node) {
            if (node == null) {
                return 0;
            }
            int left = height(node.left);
            int right = height(node.right);
            if (left < 0 || right < 0 || Math.abs(left - right) > 1) {
                return -1;
            }

            return Math.max(left, right) + 1;
        }
    }

}
