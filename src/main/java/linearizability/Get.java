package linearizability;

import kiwi.KiWiMap;

import java.util.Map;

/**
 * Created by bugabuga on 27/08/17.
 */
public class Get extends ConstDeterministicOperation<Integer> {
    public Get(int key, Integer retval){
        super(retval);
        this.key = key;
    }

    @Override
    public Integer innerOperate(Map<Integer, Integer> map) {
        return map.getOrDefault(key, null);
    }

    private Integer key;

    @Override
    public void operateKiWi(KiWiMap map) {
        retval = map.get(key);
    }

    @Override
    public boolean weakEqual(MapOperation other) {
        return other.getClass() == Get.class && ((Get)other).key == key;
    }

    @Override
    public String toString() {
        return String.format("G(%d,%d)", key, retval);
    }
}
