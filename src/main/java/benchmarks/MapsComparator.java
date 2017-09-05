package benchmarks;

import kiwi.KiWiMap;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.LongAdder;

class Throughput{
    public double oprationsThroughput;
    public double scanItemsThroughput;

    Throughput(double oprationsThroughput, double scanItemsThroughput){
        this.oprationsThroughput = oprationsThroughput;
        this.scanItemsThroughput = scanItemsThroughput;
    }
};


/**
 * Operations on both data astructures will consist of 20% insertions, 10% deletions, and 70% searches. so we are starting with
 * data structure already containing (2\3)*Integer.max_value elements.
 */
class MapsComparator {
    private final int MILLION = 1000000;
    private final int SCAN_SIZE = 32000;
    private final int itemsRange;
    private final int threadsNumber;
    private final int preFillAmount;
    private final double warmupTime;

    private Map<Integer, Integer> map;

    private final int experimentIndex;
    private LongAdder totalOperationsAdder = new LongAdder();
    private LongAdder totalItemsScanned = new LongAdder();

    private int mapNumber;
    private Random randomGenerator = new Random();

    MapsComparator(int itemsRange, int threadsNumber, int preFillAmount,
                          int mapNumber, int experimentIndex, double warmupTime) {
        assert(preFillAmount < itemsRange);
        this.preFillAmount = preFillAmount;
        this.threadsNumber = threadsNumber;
        this.itemsRange = itemsRange;
        this.mapNumber = mapNumber;
        this.experimentIndex = experimentIndex;
        this.warmupTime = warmupTime;
    }

    private void initMap() {
        switch (mapNumber){
            case 0: map = new KiWiMap();
            break;
            case 1: map = new LockFreeKSTRQ<>();
            break;
            case 2: map = new ConcurrentSkipListMap<>();
            break;
            case 3: map = new ConcurrentHashMap<>();
            break;
            default:
                assert(false);
        }
    }

    // Cause the JIT compiler to compile all the relevant code.
    void warmup(){
        System.out.println("warming up - wait for JIT compiler...");
        initMap();
        try {
            operationsThroughtput(warmupTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    private void preFillMap() {
        initMap();
        Set<Integer> items = new HashSet<>();
        while(items.size() < preFillAmount){
            int key = randomKey();
            map.put(key, randomGenerator.nextInt());
            items.add(key);
        }
    }

    private void initThreads(double duration, Thread[] threads) {
        assert(map != null);
        for (int i = 0; i < threadsNumber; i++) {
            threads[i] = new Thread(new OperationRunnable(map, i, duration));
        }
    }

    Throughput operationsThroughtput(double duration) throws InterruptedException {
        totalItemsScanned.reset();
        totalOperationsAdder.reset();

        Thread[] threads = new Thread[threadsNumber];
        preFillMap();
        initThreads(duration, threads);
        for (Thread t: threads) {
            t.start();
        }
        System.out.println("sleeping");
        Thread.sleep((long)(1000 * duration) + 100);
        for(Thread t: threads){
            double beforeJoin = System.currentTimeMillis();
            t.join();
            assert(System.currentTimeMillis() - beforeJoin < 200);
        }
        return new Throughput(totalOperationsAdder.floatValue() / (duration * MILLION),
                totalItemsScanned.floatValue() / (duration * MILLION));
    }

    int randomKey(){
        return randomGenerator.nextInt(itemsRange);
    }
    
    private class OperationRunnable implements Runnable {

        private Integer[] tempArray;
        Map map;
        int threadId;
        int operationsPerformedCount;
        Random rand;
        double runDurationSeconds;

        public OperationRunnable(Map map, int threadId, double runDurationSeconds) {
            this.map = map;
            this.threadId = threadId;
            this.runDurationSeconds = runDurationSeconds;
            tempArray = new Integer[SCAN_SIZE]; // output array for scans - avoid allocating an array for each scan.
            rand = new Random();
        }

        private void putAndRemove() {
            map.put(randomKey(), rand.nextInt());
            map.remove(randomKey());
            operationsPerformedCount +=2;
        }

        void scan(int startKey){
            if (map instanceof KiWiMap) {
                operationsPerformedCount += ((KiWiMap) map).getRange(tempArray, new Integer[0], false, startKey, startKey + SCAN_SIZE);
            }else if(map instanceof LockFreeKSTRQ){
                operationsPerformedCount += ((LockFreeKSTRQ) map).getRange(new Integer[0], tempArray, true, startKey, startKey + SCAN_SIZE);
            }else{
                System.err.println(map);
                assert(false);
            }
        }

        boolean isScanThread(){
            return experimentIndex == 2 || (experimentIndex == 3 && threadId >= threadsNumber / 2);
        }

        int randomKey(){
            return rand.nextInt(itemsRange);
        }

        void get(){
            map.get(randomKey());
            operationsPerformedCount++;
        }

        public void run() {
            double endTime = System.currentTimeMillis() + 1000 * runDurationSeconds;
            switch (experimentIndex) {
                case 0:
                    while (System.currentTimeMillis() < endTime) {
                        get();
                    }
                    break;
                case 1:
                    while (System.currentTimeMillis() < endTime) {
                        putAndRemove();
                    }
                    break;
                case 2:
                    while (System.currentTimeMillis() < endTime) {
                        scan(randomKey());
                    }
                    break;
                case 3:
                    while (System.currentTimeMillis() < endTime) {
                        if(isScanThread()) {
                            scan(randomKey());
                        }else{
                            putAndRemove();
                        }
                    }
                    break;
                default:
                    assert(false);
            }
            if(isScanThread()){
                totalItemsScanned.add(operationsPerformedCount);
            }else{
                totalOperationsAdder.add(operationsPerformedCount);
            }
        }

    }

}



