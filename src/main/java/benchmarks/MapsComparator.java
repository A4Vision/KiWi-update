package benchmarks;

import kiwi.KiWiMap;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Operations on both data astructures will consist of 20% insertions, 10% deletions, and 70% searches. so we are starting with
 * data structure already containing (2\3)*Integer.max_value elements.
 */
public class MapsComparator {

    private final int itemsRange;
    private final int operationsNumber;

    private KiWiMap kiwiMap = new KiWiMap();
    private ConcurrentHashMap<Integer, Integer> concurrentHashMap = new ConcurrentHashMap<>();
    private Thread[] kiwiThreads = new Thread[100];
    private Thread[] concurrentMapThreads = new Thread[100];
    private Random rand = new Random();

    public MapsComparator(int itemsRange, int operationsNumber) {
        this.itemsRange = itemsRange;
        this.operationsNumber = operationsNumber;

    }

    void preFillMaps() {
        for (int i = 0; i<(itemsRange /3)*2; i++) {
            int value = rand.nextInt();
            concurrentHashMap.put(value, i);
            kiwiMap.put(value, i);
        }
    }

    void initThreads() {
        for (int i=0;i<100;i++) {
            kiwiThreads[i] = new Thread(new KiwiOperationRunnable());
            concurrentMapThreads[i] = new Thread(new HashMapOperationRunnable());
        }
    }

    long performKiwiOperations() {
        long startingTime = System.currentTimeMillis();
        for (int i = 0; i< operationsNumber; i++) {
            int threadNumber = rand.nextInt(100);
            kiwiThreads[threadNumber].run();
        }
        long finishTime = System.currentTimeMillis();
        return finishTime - startingTime;
    }


    long performHashMapOperations() {
        long startingTime = System.currentTimeMillis();
        for (int i = 0; i< operationsNumber; i++) {
            int threadNumber = rand.nextInt(100);
            concurrentMapThreads[threadNumber].run();
        }
        long finishTime = System.currentTimeMillis();
        return finishTime - startingTime;
    }


    private class KiwiOperationRunnable implements Runnable {

        public void run() {
            int operation = rand.nextInt(10);
            int key = rand.nextInt(itemsRange);
            if (operation < 2) {
                int value = rand.nextInt();
                kiwiMap.put(key, value);
            } else if (operation == 2) {
                kiwiMap.remove(key);
            } else {
                kiwiMap.get(key);
            }
        }

    }


    private class HashMapOperationRunnable implements Runnable {

        public void run() {
            int operation = rand.nextInt(10);
            int key = rand.nextInt(itemsRange);
            if (operation < 2) {
                int value = rand.nextInt();
                concurrentHashMap.put(key, value);
            } else if (operation == 2) {
                concurrentHashMap.remove(key);
            } else {
                concurrentHashMap.get(key);
            }
        }

    }

    private Map<Integer, Integer> createRandomMap(int size) {
        Map<Integer, Integer> map = new HashMap<>();
        for (int i=0;i<size;i++) {
            map.put(rand.nextInt(100), rand.nextInt(200));
        }
        return map;
    }


}