package linearizability;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.*;

/**
 * Created by bugabuga on 30/08/17.
 */
public class HistoryTest {
    @Test
    public void testIsLinearizable() throws Exception {
        ArrayList<Integer> scan1 =
                new ArrayList<>( Arrays.asList(4, 4));

        ArrayList<TimedOperation> h0 = new ArrayList<>(Arrays.asList(
                new TimedOperation(new Get(12, 3), new Interval(1, 4)),
                new TimedOperation(new Get(12, 4), new Interval(5, 7)),
                new TimedOperation(new Scan(12, 20, scan1), new Interval(8.1, 8.7)),
                new TimedOperation(new Get(12, 3), new Interval(8.8, 10))));


        ArrayList<TimedOperation> h1 = new ArrayList<>(Arrays.asList(
                new TimedOperation(new Put(12, 3), new Interval(1, 5.1)),
                new TimedOperation(new Put(12, 4), new Interval(6, 7.2)),
                new TimedOperation(new Put(10, 4), new Interval(7.3, 7.4)),
                new TimedOperation(new Put(11, 4), new Interval(7.5, 7.6)),
                new TimedOperation(new Put(13, 4), new Interval(8, 8.2)),
                new TimedOperation(new Put(12, 3), new Interval(9.5, 12))));

        History h = new History(new ArrayList<>(Arrays.asList(h0, h1)));
        assertTrue(h.isLinearizable());
        // Scan starts after Put(13, 4)
        h0.get(2).interval = new Interval(7.5, 7.9);
        assertFalse(h.isLinearizable());
        h0.get(2).interval = new Interval(8.1, 8.7);
        // As before.
        assertTrue(h.isLinearizable());
        // Last get returns a false value.
        ((Get)h0.get(3).operation).setRetval(1);
        assertFalse(h.isLinearizable());
    }

    @Test
    public void testDiscard() throws Exception {
        ArrayList<TimedOperation> h0 = new ArrayList<>(Collections.singletonList(
                new TimedOperation(new Put(1, 0), new Interval(1, 2))));

        ArrayList<TimedOperation> h1 = new ArrayList<>(Collections.singletonList(
                new TimedOperation(new Discard(1), new Interval(2.1, 3))));

        ArrayList<TimedOperation> h2 = new ArrayList<>(Arrays.asList(
                new TimedOperation(new Get(1, 0), new Interval(2.1, 2.5)),
                new TimedOperation(new Get(1, null), new Interval(2.6, 4))));

        History h = new History(new ArrayList<>(Arrays.asList(h0, h1, h2)));
        assertTrue(h.isLinearizable());
        // Scan starts after Put(13, 4)
        ((Get)h2.get(1).operation).setRetval(0);
        assertTrue(h.isLinearizable());
        h2.get(1).interval.start = 3.5;
        assertFalse(h.isLinearizable());
    }


    @Test
    public void testSize() throws Exception {
        ArrayList<TimedOperation> h0 = new ArrayList<>(Collections.singletonList(
                new TimedOperation(new Put(1, 0), new Interval(1, 2))));

        ArrayList<TimedOperation> h1 = new ArrayList<>(Collections.singletonList(
                new TimedOperation(new Size(1), new Interval(2.1, 3))));

        ArrayList<TimedOperation> h2 = new ArrayList<>(Collections.singletonList(
                new TimedOperation(new Size(0), new Interval(1, 1.5))));

        History h = new History(new ArrayList<>(Arrays.asList(h0, h1, h2)));
        assertTrue(h.isLinearizable());
        // Scan starts after Put(13, 4)
        ((Size)h2.get(0).operation).setRetval(1);
        assertTrue(h.isLinearizable());
        ((Size)h2.get(0).operation).setRetval(0);
        h2.get(0).interval.start = 2.1;
        h2.get(0).interval.end = 3.;
        assertFalse(h.isLinearizable());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidation(){
        // Can't use the same operation twice because then undo() might not work properly.
        Put p = new Put(1, 0);
        ArrayList<TimedOperation> h0 = new ArrayList<>(Arrays.asList(
                new TimedOperation(p, new Interval(1, 2)),
                new TimedOperation(p, new Interval(3, 4))));
        History h = new History(new ArrayList<>(Collections.singletonList(h0)));
    }

    @Test
    public void testGetInsidePut(){
        ArrayList<TimedOperation> h0 = new ArrayList<>(Arrays.asList(
                new TimedOperation(new Put(0, 1), new Interval(0, 1)),
                new TimedOperation(new Put(0, 2), new Interval(2, 5))));
        ArrayList<TimedOperation> h1 = new ArrayList<>(Collections.singletonList(
                new TimedOperation(new Get(0, 1), new Interval(3, 4))));
        History h = new History(new ArrayList<>(Arrays.asList(h0, h1)));
        System.out.println(h);
        assertTrue(h.isLinearizable());
    }
}
