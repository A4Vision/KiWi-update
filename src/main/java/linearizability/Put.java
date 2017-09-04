package linearizability;

import kiwi.KiWiMap;

import java.util.Map;

/**
 * Created by bugabuga on 27/08/17.
 */
public class Put implements MapOperation {
    private int key;
    private int value;
    private Integer prevValue;

    public Put(int key, int value){
        this.key = key;
        this.value = value;
    }

    @Override
    public void operate(Map<Integer, Integer> map) {
        prevValue = map.getOrDefault(key, null);
        map.put(key, value);
    }

    @Override
    public void undo(Map<Integer, Integer> map) {
        map.remove(key);
        if(prevValue != null){
            map.put(key, prevValue);
        }
    }

    @Override
    public boolean isConst()    {
        return false;
    }

    @Override
    public boolean validate() {
        return true;
    }

    @Override
    public void operateKiWi(KiWiMap map) {
        map.put(key, value);
    }

    @Override
    public boolean weakEqual(MapOperation other) {
        return other.getClass() == Put.class && ((Put)other).key == key && ((Put)other).value == value;
    }

    @Override
    public String toString() {
        return String.format("P(%d,%d)", key, value);
    }
}
