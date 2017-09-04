package kiwi;

import linearizability.*;
import org.junit.Test;
import util.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.*;


/**
 * Created by bugabuga on 01/09/17.
 */
public class KiwiLoggingTest {
    @Test
    public void loggingOfKiwiMap() throws IOException, InterruptedException {
        String tempFolder = Utils.tempFolder();
        KiWiMap map = new KiWiMap(true, true);
        ArrayList<ArrayList<MapOperation>> mapOperations = new ArrayList<>();
        mapOperations.add(new ArrayList<>(Arrays.asList(
                new Get(0, null), new Put(2, 3), new SizeLowerBound(null)
        )));
        mapOperations.add(new ArrayList<>(Arrays.asList(
                new Get(0, null), new Put(2, 3), new SizeUpperBound(null),
                new Scan(10, 20, null)
        )));
        createActivity(map, mapOperations);
        map.write(tempFolder);
        System.out.println(tempFolder);

        HistoryJsonReader reader = new HistoryJsonReader(tempFolder, mapOperations.size());
        History history = reader.read();
        ArrayList<ArrayList<TimedOperation>> histories = history.getHistories();

        assertEquals(mapOperations.size(), histories.size());
        for(int i = 0; i < mapOperations.size(); ++i){
            System.out.println(i);
            assertEquals(mapOperations.get(i).size(), histories.get(i).size());
            for(int j = 0; j < mapOperations.get(i).size(); ++j){
                MapOperation expected = mapOperations.get(i).get(j);
                MapOperation actual = histories.get(i).get(j).operation;
                assertTrue(expected.weakEqual(actual));
            }
        }


    }

    private void createActivity(KiWiMap map, ArrayList<ArrayList<MapOperation>> mapOperations) throws InterruptedException {
        ArrayList<Thread> threads = new ArrayList<>();
        for(ArrayList<MapOperation> operations: mapOperations){
            Thread t = new Thread(new ThreadOperateOnMap(map, operations));
            threads.add(t);
            t.start();
        }
        for (Thread thread : threads) {
            thread.join();
        }
    }
}


