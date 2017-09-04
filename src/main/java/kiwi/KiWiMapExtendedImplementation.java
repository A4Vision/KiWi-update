package kiwi;

import util.IntegerGenerator;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by gum on 9/2/2017.
 */
public class KiWiMapExtendedImplementation extends KiWiMap {


    @Override
    @Deprecated
    public boolean isEmpty() throws RuntimeException {
        if (sizeUpperBound() == 0) {
            return true;
        } else if (sizeLowerBound() >= 1) {
            return false;
        }
        throw new RuntimeException("cannot validate current map state.");
    }

    @Override
    public Set<Integer> keySet() {
        final int SPARE = 100;
        Integer[] resultKeys = new Integer[sizeUpperBound() + SPARE];
        Integer[] resultValues = new Integer[sizeUpperBound() + SPARE];
        // TODO(bugabuga): return an ArrayList<Integer> from getRange.
        int setSize = getRange(resultValues, resultKeys, true, Integer.MIN_VALUE, Integer.MAX_VALUE);
        return new HashSet<>(Arrays.asList(Arrays.copyOfRange(resultKeys,0,setSize)));
    }

    @Override
    public Set<Integer> values() {
        Integer[] resultValues = new Integer[sizeUpperBound()];
        int setSize = getRange(resultValues, null, false, Integer.MIN_VALUE, Integer.MAX_VALUE);
        return new HashSet<>(Arrays.asList(Arrays.copyOfRange(resultValues,0,setSize)));
    }

    @Override
    @Deprecated
    public int size() throws RuntimeException {
        int lowerBound = sizeLowerBound();
        int upperBound = sizeUpperBound();
        if (lowerBound >= upperBound) {
            return upperBound;
        } else { //try again
            lowerBound = sizeLowerBound();
            if (upperBound <= lowerBound) {
                return lowerBound;
            }
        }
        throw new RuntimeException("current state: unknown size.");
    }



}
