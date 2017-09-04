package linearizability;

import kiwi.KiWi;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by bugabuga on 01/09/17.
 */
public class HistoryLogger{
    ArrayList<ArrayList<TimedOperation>> operationsLists;

    public HistoryLogger(){
        operationsLists = new ArrayList<>();
        for(int i = 0; i < KiWi.MAX_THREADS; ++i){
            operationsLists.add(new ArrayList<TimedOperation>());
        }
    }

    public void logOperation(TimedOperation op){
        operationsLists.get(KiWi.threadId()).add(op);
    }

    public void write(String dir) throws IOException {
//        System.out.println(KiWi.threadId());
        int index = 0;
        for(ArrayList<TimedOperation> ops: operationsLists){
            if(!ops.isEmpty()){
                HistoryJsonWriter writer = new HistoryJsonWriter(dir, index);
                index++;
                for(TimedOperation op: ops){
                    writer.addOperation(op);
                }
                writer.write();
            }
        }
    }
}
