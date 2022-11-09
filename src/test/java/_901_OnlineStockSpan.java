import org.junit.Test;

import java.util.Stack;

import static org.junit.Assert.assertEquals;

public class _901_OnlineStockSpan {

    @Test
    public void test_901_OnlineStockSpan() {

        StockSpanner stockSpanner = new StockSpanner();
        assertEquals(1, stockSpanner.next(100)); // return 1
        assertEquals(1, stockSpanner.next(80));  // return 1
        assertEquals(1, stockSpanner.next(60));  // return 1
        assertEquals(2, stockSpanner.next(70));  // return 2
        assertEquals(1, stockSpanner.next(60));  // return 1
        assertEquals(4, stockSpanner.next(75));  // return 4, because the last 4 prices (including today's price of 75) were less than or equal to today's price.
        assertEquals(6, stockSpanner.next(85));  // return 6

    }

}

class StockSpanner {

    Stack<int[]> stock;

    public StockSpanner() {
        this.stock = new Stack<>();
    }

    public int next(int price) {
        var counter = 1;
        while (stock.size() > 0 && price >= stock.peek()[0]) {
            counter += stock.pop()[1];
        }
        stock.push(new int[]{price, counter});

        return counter;
    }
}