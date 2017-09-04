package linearizability;

import kiwi.KiWiMap;

import java.util.Map;

/**
 * Created by bugabuga on 27/08/17.
 */
public class Discard implements MapOperation {
    // The key to discard
    private int key;
    // The previous value mapped to this key (before the actual delete)
    private Integer prevValue;

    public Discard(int key){
        this.key = key;
        prevValue = null;
    }

    @Override
    public void operate(Map<Integer, Integer> map) {
        if(map.containsKey(key)){
            prevValue = map.get(key);
            map.remove(key);
        }
    }

    @Override
    public void undo(Map<Integer, Integer> map) {
        if(prevValue != null){
            map.put(key, prevValue);
            prevValue = null;
        }
    }

    @Override
    public boolean isConst() {
        return false;
    }

    @Override
    public boolean validate() {
        return true;
    }

    @Override
    public void operateKiWi(KiWiMap map) {
        map.remove(key);
    }

    @Override
    public boolean weakEqual(MapOperation other) {
        return other.getClass() == Discard.class && ((Discard)other).key == key;
    }

    @Override
    public String toString() {
        return String.format("D(%d)", key);
    }
}
