package leetcode;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class _0019_RemoveNthNodeFromEndOfList {

    @Test
    public void testSolution1() {

        var s = new Solution();

        var l5 = new ListNode(5);
        var l4 = new ListNode(4, l5);
        var l3 = new ListNode(3, l4);
        var l2 = new ListNode(2, l3);
        var l1 = new ListNode(1, l2);
        var res = s.removeNthFromEnd(l1, 2);

        assertEquals("1 2 3 5", res.toString());
    }

    @Test
    public void testSolution2() {
        var n1 = new ListNode(1);

        var s = new Solution();
        var res = s.removeNthFromEnd(n1, 1);

        assertNull(res);
    }

    @Test
    public void testSolution3() {
        var n2 = new ListNode(2);
        var n1 = new ListNode(1, n2);

        var s = new Solution();
        var res = s.removeNthFromEnd(n1, 1);

        assertEquals("1", res.toString());
    }

    static class Solution {
        public ListNode removeNthFromEnd(ListNode head, int n) {
            var headOfHead = new ListNode(Integer.MIN_VALUE, head);
            var turtle = headOfHead;
            var rabbit = head;

            while (rabbit != null && n > 0) {
                n--;
                rabbit = rabbit.next;
            }

            while (rabbit != null) {
                turtle = turtle.next;
                rabbit = rabbit.next;
            }

            turtle.next = turtle.next.next;
            return headOfHead.next;
        }
    }


}