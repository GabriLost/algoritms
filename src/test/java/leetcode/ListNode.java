package leetcode;

// Definition for singly-linked list.
public class ListNode {
    int val;
    ListNode next;

    ListNode() {
    }

    ListNode(int val) {
        this.val = val;
    }

    ListNode(int val, ListNode next) {
        this.val = val;
        this.next = next;
    }

    public String toString() {
        var b = new StringBuilder();
        var ptr = this;
        while (ptr.next != null) {
            b.append(ptr.val).append(" ");
            ptr = ptr.next;
        }
        b.append(ptr.val);
        return b.toString();
    }
}