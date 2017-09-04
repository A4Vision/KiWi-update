package kiwi;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * Created by bugabuga on 01/09/17.
 */
public class TestKiWiNaive {
    @Test
    public void testPutAndScan(){
        KiWiMap map = new KiWiMap();
        map.put(3, 4);
        Integer[] r = new Integer[10];
        int size = map.getRange(r, null, false, 0, 2);
        assertEquals(0, size);
    }

    @Test
    public void testPutTwiceAndScan(){
        KiWiMap map = new KiWiMap();
        Integer[] r = new Integer[10];
        map.put(2, 1);
        map.getRange(r, null, false, 0, 2);
        map.put(2, 8);
        map.getRange(r, null, false, 0, 2);
        map.put(2, 0);
        int size = map.getRange(r, null, false, 0, 2);
        assertEquals(1, size);
    }

    @Test
    public void testPutAfterRebalance(){
        KiWiMap map = new KiWiMap();
        for(int i = 0; i < 5000; ++i){
            map.put(i, i);
        }
        assertEquals(new Integer(499), map.get(499));
        // Now, increase version and put:
        Integer [] temp = new Integer[10];
        map.getRange(temp, null, false, 0, 0);
        map.put(499, -1);
        assertEquals(new Integer(-1), map.get(499));
        // Next, increase version again and put
        map.getRange(temp, null, false, 0, 0);
        map.put(499, -2);
        // Then get
        assertEquals(new Integer(-2), map.get(499));

        assertEquals(1, map.getRange(temp, null, false, 499, 499));
        assertEquals(new Integer(-2), temp[0]);
    }

    @Test
    public void testSizeBoundsDuringRebalance(){
        KiWiMap map = new KiWiMap(false, true);
        final int N = 5000;
        for(int i = 0; i < N; ++i){
            map.put(i, i);
            assertEquals(i + 1, map.sizeLowerBound());
            assertEquals(i + 1, map.sizeUpperBound());
        }
        for(int i = 0; i < N; ++i){
            map.remove(i);
            assertEquals(N - i - 1, map.sizeLowerBound());
            assertEquals(N - i - 1, map.sizeUpperBound());
        }
    }
}
