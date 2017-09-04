package linearizability;

import kiwi.KiWiMap;

import java.util.Map;

/**
 * Created by bugabuga on 01/09/17.
 */
public class SizeLowerBound extends ConstDeterministicOperation<Integer> {
    public SizeLowerBound(Integer retval) {
        super(retval);
    }

    @Override
    Integer innerOperate(Map<Integer, Integer> map) {
        return map.size();
    }

    @Override
    public boolean validate() {
        return retval <= actualRetval;
    }

    @Override
    public void operateKiWi(KiWiMap map) {
        retval = map.sizeLowerBound();
    }

    @Override
    public boolean weakEqual(MapOperation other) {
        return other.getClass() == SizeLowerBound.class;
    }

    @Override
    public String toString() {
        return String.format("L(%d)", retval);
    }
}
