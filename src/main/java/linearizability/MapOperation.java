package linearizability;

import kiwi.KiWiMap;

import java.util.Map;

/**
 * Created by bugabuga on 27/08/17.
 */
public interface MapOperation {
    void operate(Map<Integer, Integer> map);

    void undo(Map<Integer, Integer> map);

    boolean isConst();

    boolean validate();

    void operateKiWi(KiWiMap map);

    boolean weakEqual(MapOperation other);
};
