package linearizability;

import kiwi.KiWiMap;

import java.util.Map;

/**
 * Created by bugabuga on 27/08/17.
 */
class Size extends ConstDeterministicOperation<Integer> {
    Size(Integer retval) {
        super(retval);
    }

    @Override
    Integer innerOperate(Map<Integer, Integer> map) {
        return map.size();
    }


    @Override
    public void operateKiWi(KiWiMap map) {
        map.size();
    }

    @Override
    public boolean weakEqual(MapOperation other) {
        return other.getClass() == Size.class;
    }
}
