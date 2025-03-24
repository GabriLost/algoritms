
package leetcode;

import static org.junit.Assert.assertEquals;

import java.util.LinkedList;

import org.junit.Test;

public class _0111_MinimumDepthOfBinaryTree {

    @Test
    public void testSolution() {

        var solution = new Solution();

        Integer[] arr1 = { 3, 9, 20, null, null, 15, 7 };
        Integer[] arr2 = { 1, null, 2, null, 3, null, 4, null, 5 };

        assertEquals(2, solution.minDepth(new TreeNode(arr1)));
        assertEquals(5, solution.minDepth(new TreeNode(arr2)));

    }

    static class Solution {
        public int minDepth(TreeNode root) {
            if (root == null) {
                return 0;
            }

            var queue = new LinkedList<TreeNode>();

            queue.add(root);
            var depth = 1;

            while (!queue.isEmpty()) {
                int size = queue.size();
                while (size > 0) {
                    var node = queue.poll();
                    if (node.left == null && node.right == null)
                        return depth;
                    if (node.left != null)
                        queue.add(node.left);
                    if (node.right != null)
                        queue.add(node.right);
                    size--;
                }
                depth++;
            }

            return depth;
        }
    }
}