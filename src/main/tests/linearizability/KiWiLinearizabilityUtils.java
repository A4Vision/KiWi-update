package linearizability;

import kiwi.KiWiMap;
import kiwi.ThreadOperateOnMap;
import org.junit.Test;
import util.Utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import static org.junit.Assert.assertTrue;

/**
 * Created by bugabuga on 02/09/17.
 */
public class KiWiLinearizabilityUtils {

    static ArrayList<MapOperation> randomOperations(int seed, int keyRange,
                                                    int operationsAmount,
                                                    double putPercentange,
                                                    double getPercentage, double lowerSizePrecentage, double upperSizePrecentage,
                                                    double discardPercentage){
        Random generator = new Random(seed);

        ArrayList<MapOperation> res = new ArrayList<>();
        for(int i = 0; i < operationsAmount; ++i){
            double r = generator.nextDouble();
            if(r < putPercentange){
                res.add(new Put(generator.nextInt(keyRange), generator.nextInt(100)));
            }else if(r < putPercentange + getPercentage){
                res.add(new Get(generator.nextInt(keyRange), null));
            }else if (r < putPercentange + getPercentage + lowerSizePrecentage){
                res.add(new SizeLowerBound(null));
            }else if (r < putPercentange + getPercentage + lowerSizePrecentage + upperSizePrecentage){
                res.add(new SizeUpperBound(null));
            }else if (r < putPercentange + getPercentage + lowerSizePrecentage + upperSizePrecentage + discardPercentage) {
                res.add(new Discard(generator.nextInt(keyRange)));
            }else{
                res.add(new Scan(1, generator.nextInt(keyRange) + 1, null));
            }
        }
        return res;
    }


    static void KiWiIsLinearizableWithSpecificOperations(ArrayList<ArrayList<MapOperation>> opsLists) throws InterruptedException, IOException {
        KiWiMap map = new KiWiMap(true, true);
        ArrayList<Thread> threads = new ArrayList<>();
        String tempFolder = Utils.tempFolder();
        for (ArrayList<MapOperation> ops: opsLists) {
            Thread t = new Thread(new ThreadOperateOnMap(map, ops));
            t.start();
            threads.add(t);
        }
        for (Thread t: threads) {
            t.join();
        }
        map.write(tempFolder);

        HistoryJsonReader reader = new HistoryJsonReader(tempFolder, threads.size());
        History history = reader.read();
        boolean isLinearizable = history.isLinearizable();
        if(!isLinearizable) {
            System.out.println(history);
            System.out.println(tempFolder);
            assertTrue(false);
        }else {
            Utils.clearFolder(tempFolder);
        }
    }

    static void KiWiIsLinearizableOperationsFromHistoryFiles(String[] folders) throws InterruptedException, IOException {
        for(String folderPath: folders) {
            File folder = new File(folderPath);
            if(!folder.exists() || !folder.isDirectory())
                continue;
            int numThreads = HistoryJsonReader.getNumThreads(folder);
            if(numThreads == 0){
                continue;
            }
            HistoryJsonReader reader = new HistoryJsonReader(folder.toString(), numThreads);
            History history = reader.read();
            ArrayList<ArrayList<MapOperation>> opsLists = new ArrayList<>();
            for (ArrayList<TimedOperation> timedOps : history.getHistories()) {
                ArrayList<MapOperation> ops = new ArrayList<>();
                for (TimedOperation timedOp : timedOps) {
                    ops.add(timedOp.operation);
                }
                opsLists.add(ops);
            }
            for (int j = 0; j < 100; ++j) {
                KiWiIsLinearizableWithSpecificOperations(opsLists);
            }
        }
    }

    static void KiWiIsLinearizableRandomOperations(int seed, int keyRange, int operationsAmount, int numThreads,
                                                   double putPrecentage, double getPrecentage,
                                                   double lowerSizePrecentage, double upperSizePrecentage,
                                                   double discardPercentage) throws InterruptedException, IOException {
        ArrayList<ArrayList<MapOperation>> opsLists = new ArrayList<>();
        for(int i = 0; i < numThreads; ++i){
            ArrayList<MapOperation> ops = KiWiLinearizabilityUtils.randomOperations(seed * numThreads + i,
                    keyRange, operationsAmount, putPrecentage, getPrecentage, lowerSizePrecentage, upperSizePrecentage,
                    discardPercentage);
            opsLists.add(ops);
        }
        KiWiLinearizabilityUtils.KiWiIsLinearizableWithSpecificOperations(opsLists);
    }
}
