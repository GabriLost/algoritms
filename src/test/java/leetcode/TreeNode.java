package leetcode;

import java.util.LinkedList;
import java.util.Queue;

public class TreeNode {
    Integer val;
    TreeNode left;
    TreeNode right;

    TreeNode() {
    }

    TreeNode(Integer val) {
        this.val = val;
    }

    TreeNode(Integer val, TreeNode left, TreeNode right) {
        this.val = val;
        this.left = left;
        this.right = right;
    }

    TreeNode(Integer[] arr) {
        TreeNode root = this.buildTree(arr);
        this.val = root.val;
        this.left = root.left;
        this.right = root.right;
    }

    public TreeNode buildTree(Integer[] arr) {
        if (arr == null || arr.length == 0) {
            return null;
        }

        TreeNode root = new TreeNode(arr[0]);
        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);

        int i = 1;
        while (!queue.isEmpty() && i < arr.length) {
            TreeNode curr = queue.poll();

            if (arr[i] != null) {
                curr.left = new TreeNode(arr[i]);
                queue.offer(curr.left);
            }
            i++;
            if (i < arr.length && arr[i] != null) {
                curr.right = new TreeNode(arr[i]);
                queue.offer(curr.right);
            }
            i++;
        }

        return root;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        var queue = new LinkedList<TreeNode>();
        queue.offer(this);
        Integer depth = 1;
        var empties = 0;
        while (!queue.isEmpty()) {
            int levelSize = queue.size();
            for (int i = 0; i < levelSize; i++) {
                TreeNode currNode = queue.poll();

                if (currNode != null) {
                    sb.append("(" + currNode.val + ") ");
                    if (currNode.left != null) {
                        sb.append("|");
                    } else {
                        sb.append("");
                        empties++;
                    }
                    if (currNode.right != null) {
                        sb.append("\\");
                    } else {
                        sb.append("");
                        empties++;
                    }

                    queue.offer(currNode.left);
                    queue.offer(currNode.right);
                }

                // add spacing between nodes
                if (i == levelSize-1) {
                    sb.append("\n");
                    sb.append("    ".repeat(empties));
                }
            }

            depth++;
        }
        sb.append("\n");
        return sb.toString();
    }

}