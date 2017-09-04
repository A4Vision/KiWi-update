package linearizability;

import kiwi.KiWiMap;
import util.Utils;

import java.util.*;

/**
 * Created by bugabuga on 27/08/17.
 */
public class Scan extends ConstDeterministicOperation<ArrayList<Integer>> {
    public Scan(int start_key, int end_key, ArrayList<Integer> retval) {
        super(retval);
        startKey = start_key;
        endKey = end_key;
    }

    @Override
    ArrayList<Integer> innerOperate(Map<Integer, Integer> map) {
        ArrayList<Integer> res = new ArrayList<>();
        TreeSet<Integer> sortedSet = new TreeSet<>();
        sortedSet.addAll(map.keySet());
        // Iterate over the keys, sorted.
        for (Integer key : sortedSet) {
            if(startKey <= key && key <= endKey)
                res.add(map.get(key));
        }
        return res;
    }
    private int startKey;
    private int endKey;

    @Override
    public void operateKiWi(KiWiMap map) {
        Integer[] result = new Integer[1000];
        int length = map.getRange(result, null, false, startKey, endKey);
        retval = Utils.convertArrayToList(result, length);
    }

    @Override
    public String toString() {
        return String.format("S%d,%d->%s", startKey, endKey, retval);
    }

    @Override
    public boolean weakEqual(MapOperation other) {
        return other.getClass() == Scan.class && ((Scan)other).startKey == startKey && ((Scan)other).endKey == endKey;
    }
}
