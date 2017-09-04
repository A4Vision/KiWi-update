package linearizability;

import kiwi.KiWiMap;
import kiwi.ThreadOperateOnMap;
import org.junit.Test;
import util.Utils;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Random;

/**
 * Created by bugabuga on 01/09/17.
 */


public class KiwiLinearizabilityTest {
    private static final int NUM_ITERATIONS = 100;

    @Test
    public void explicitTest1() throws IOException, InterruptedException {
        ArrayList<MapOperation> ops0 = new ArrayList<>(Arrays.asList(
                new Put(1, 2), new Put(1, 3), new Scan(1, 2, null)));
        ArrayList<MapOperation> ops1 = new ArrayList<>(Arrays.asList(
                new Put(3, 4), new Put(1, 4), new Scan(1, 2, null)));
        for(int j = 0; j < NUM_ITERATIONS; ++j) {
            KiWiLinearizabilityUtils.KiWiIsLinearizableWithSpecificOperations(new ArrayList<>(Arrays.asList(ops0, ops1)));
        }
    }

    @Test
    public void explicitTest3() throws IOException, InterruptedException {
        ArrayList<MapOperation> ops0 = new ArrayList<>(Arrays.asList(
                new Put(1, 3), new Scan(1, 1, null)));
        ArrayList<MapOperation> ops1 = new ArrayList<>(Arrays.asList(
                new Put(1, 5), new Scan(1, 1, null)));
        for(int j = 0; j < NUM_ITERATIONS; ++j) {
            KiWiLinearizabilityUtils.KiWiIsLinearizableWithSpecificOperations(new ArrayList<>(Arrays.asList(ops0, ops1)));
        }
    }

    @Test
    public void explicitTest5() throws IOException, InterruptedException {
        ArrayList<MapOperation> ops0 = new ArrayList<>(Collections.singletonList(
                (MapOperation) new Put(1, 3)));

        ArrayList<MapOperation> ops1 = new ArrayList<>(Arrays.asList(
                (MapOperation)new Scan(1, 1, null),
                new Get(1, null)));

        for(int j = 0; j < NUM_ITERATIONS; ++j) {
            KiWiLinearizabilityUtils.KiWiIsLinearizableWithSpecificOperations(new ArrayList<>(Arrays.asList(ops0, ops1)));
        }
    }

    @Test
    public void explicitTest4() throws IOException, InterruptedException {
        ArrayList<MapOperation> ops0 = new ArrayList<>(Arrays.asList(
                new Put(1, 3), new Get(1, null)));
        ArrayList<MapOperation> ops1 = new ArrayList<>(Arrays.asList(
                new Put(1, 5), new Get(1, null)));
        for(int j = 0; j < NUM_ITERATIONS; ++j) {
            KiWiLinearizabilityUtils.KiWiIsLinearizableWithSpecificOperations(new ArrayList<>(Arrays.asList(ops0, ops1)));
        }
    }

    @Test
    public void explicitTest2() throws IOException, InterruptedException {
        ArrayList<MapOperation> ops0 = new ArrayList<>(Arrays.asList(
                new Put(1, 2), new Put(1, 3), new Scan(1, 1, null)));
        ArrayList<MapOperation> ops1 = new ArrayList<>(Arrays.asList(
                new Put(1, 4), new Put(1, 3), new Put(1, 3), new Scan(1, 1, null)));
        for(int j = 0; j < NUM_ITERATIONS; ++j) {
            KiWiLinearizabilityUtils.KiWiIsLinearizableWithSpecificOperations(new ArrayList<>(Arrays.asList(ops0, ops1)));
        }
    }

    @Test
    public void explicitTest6() throws IOException, InterruptedException {
        ArrayList<MapOperation> ops0 = new ArrayList<>(Arrays.asList(
                new Put(1, 4), (MapOperation)new Put(1, 2)));
        ArrayList<MapOperation> ops1 = new ArrayList<>(Arrays.asList(
                new Put(2, 7), new Get(1, null), new Put(1, 3), new Scan(1, 1, null)));
        for(int j = 0; j < NUM_ITERATIONS; ++j) {
            KiWiLinearizabilityUtils.KiWiIsLinearizableWithSpecificOperations(new ArrayList<>(Arrays.asList(ops0, ops1)));
        }
    }

    @Test
    public void explicitTest7() throws IOException, InterruptedException {
        ArrayList<MapOperation> ops0 = new ArrayList<>(Arrays.asList(
                new Put(0, 0), new Get(0, null), new Put(1, 3), new Get(0, null), new Get(0, null), new Get(0, null), new Get(1, null), new Scan(1, 1, null)));
        ArrayList<MapOperation> ops1 = new ArrayList<>(Arrays.asList(
                new Put(0, 0), new Get(0, null), new Put(1, 2)));
        for(int j = 0; j < NUM_ITERATIONS; ++j) {
            KiWiLinearizabilityUtils.KiWiIsLinearizableWithSpecificOperations(new ArrayList<>(Arrays.asList(ops0, ops1)));
        }
    }

    @Test
    public void explicitTest8() throws IOException, InterruptedException {
        ArrayList<MapOperation> ops0 = new ArrayList<>(Arrays.asList(
                new Put(10, 11), new Put(1, 2)));
        ArrayList<MapOperation> ops1 = new ArrayList<>(Arrays.asList(
                new Put(10, 11), new Scan(1, 0, null), new Put(1, 3)));
        for(int j = 0; j < NUM_ITERATIONS; ++j) {
            KiWiLinearizabilityUtils.KiWiIsLinearizableWithSpecificOperations(new ArrayList<>(Arrays.asList(ops0, ops1)));
        }
    }

    @Test
    public void explicitTest9() throws IOException, InterruptedException {
        ArrayList<MapOperation> ops0 = new ArrayList<>(Arrays.asList(
                new Put(1, 2)));
        ArrayList<MapOperation> ops1 = new ArrayList<>(Arrays.asList(
                new Get(1, null), new Discard(1), new Get(1, null)));
        for(int j = 0; j < NUM_ITERATIONS; ++j) {
            KiWiLinearizabilityUtils.KiWiIsLinearizableWithSpecificOperations(new ArrayList<>(Arrays.asList(ops0, ops1)));
        }
    }

    @Test
    public void testsFromFiles() throws IOException, InterruptedException {
//        String[] files = {Paths.get(System.getProperty("user.home"), "problematic_histories", "history_54173420026714").toString()};
        String[] files = {"/tmp/history_62913341771089", "/tmp/history_62902665448097"};
        KiWiLinearizabilityUtils.KiWiIsLinearizableOperationsFromHistoryFiles(files);
    }

    @Test
    public void randomPutGetScanTest() throws IOException, InterruptedException {
        for(int j = 0; j < NUM_ITERATIONS; ++j) {
            if (j % 100 == 0) {
                System.out.println(j);
            }
            KiWiLinearizabilityUtils.KiWiIsLinearizableRandomOperations(j, 3, 5, 4,
                    0.6, 0.2, 0., 0., 0.);
        }
    }

    @Test
    public void randomPutGetScanDiscardTest() throws IOException, InterruptedException {
        for(int j = 0; j < NUM_ITERATIONS; ++j) {
            if (j % 100 == 0) {
                System.out.println(j);
            }
            KiWiLinearizabilityUtils.KiWiIsLinearizableRandomOperations(j, 2, 4, 2,
                    0.4, 0.2, 0., 0., 0.3);
        }
    }

}

