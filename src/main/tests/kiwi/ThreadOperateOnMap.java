package kiwi;

import linearizability.MapOperation;

import java.util.ArrayList;

public class ThreadOperateOnMap implements Runnable {
    private KiWiMap map;
    private ArrayList<MapOperation> ops;

    public ThreadOperateOnMap(KiWiMap map, ArrayList<MapOperation> ops) {
        this.ops = ops;
        this.map = map;
    }

    @Override
    public void run() {
        for(MapOperation op: ops){
            op.operateKiWi(map);
        }
    }
}
