package linearizability;

import java.util.Map;

/**
 * Created by bugabuga on 27/08/17.
 */
abstract public class ConstDeterministicOperation<RetType> implements MapOperation {
    ConstDeterministicOperation(RetType retval){
        this.retval = retval;
        actualRetval = null;
    }

    abstract RetType innerOperate(Map<Integer, Integer> map);

    @Override
    public void operate(Map<Integer, Integer> map) {
        if(actualRetval != null){
            throw new UnsupportedOperationException("");
        }
        actualRetval = innerOperate(map);
    }

    @Override
    public void undo(Map<Integer, Integer> map) {
        actualRetval = null;
    }

    @Override
    public boolean isConst() {
        return true;
    }

    @Override
    public boolean validate() {
        if(retval == null){
            return actualRetval == null;
        }else {
            return retval.equals(actualRetval);
        }
    }

    public void setRetval(RetType retval){
        this.retval = retval;
    }

    RetType retval;
    RetType actualRetval;
}
