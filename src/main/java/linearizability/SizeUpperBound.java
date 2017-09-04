package linearizability;

import kiwi.KiWiMap;

import java.util.Map;

/**
 * Created by bugabuga on 01/09/17.
 */
public class SizeUpperBound extends ConstDeterministicOperation<Integer> {
    public SizeUpperBound(Integer retval) {
        super(retval);
    }

    @Override
    Integer innerOperate(Map<Integer, Integer> map) {
        return map.size();
    }

    @Override
    public boolean validate() {
        return retval >= actualRetval;
    }

    @Override
    public void operateKiWi(KiWiMap map) {
        retval = map.sizeUpperBound();
    }

    @Override
    public boolean weakEqual(MapOperation other) {
        return other.getClass() == SizeUpperBound.class;
    }

    @Override
    public String toString() {
        return String.format("U(%d)", retval);
    }

}
