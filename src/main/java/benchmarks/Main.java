package benchmarks;

import java.util.ArrayList;

/**
 * Created by gum on 8/21/2017.
 */
public class Main {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("parameters:\n\titemsRange threadsAmount preFillAmount mapNumber experimentIndex experimentsCount testDurationSeconds warmUpSeconds");
        System.out.println(args.length);
        assert(args.length >= 8);
        int itemsRange = Integer.parseInt(args[0]);
        int threadsAmount = Integer.parseInt(args[1]);
        int preFillAmount = Integer.parseInt(args[2]);
        int mapNumber = Integer.parseInt(args[3]);
        int experimentIndex = Integer.parseInt(args[4]);
        int experimentsCount = Integer.parseInt(args[5]);
        double testDurationSeconds = Double.parseDouble(args[6]);
        double warmUpSeconds = Double.parseDouble(args[7]);

        System.out.format("itemsRange=%d threadsAmount=%d preFillAmount=%d mapNumber=%d experimentIndex=%d experimentsCount=%d testDurationSeconds=%.2f warmUpSeconds=%.2f\n",
                itemsRange, threadsAmount, preFillAmount, mapNumber, experimentIndex, experimentsCount, testDurationSeconds, warmUpSeconds);

        MapsComparator comparator = new MapsComparator(itemsRange, threadsAmount, preFillAmount,
                mapNumber, experimentIndex, warmUpSeconds);
        comparator.warmup();
        ArrayList<Double> operations = new ArrayList<>();
        ArrayList<Double> scans = new ArrayList<>();
        for(int i = 0; i < experimentsCount; ++i){
            Throughput t = comparator.operationsThroughtput(testDurationSeconds);
            operations.add(t.oprationsThroughput);
            scans.add(t.scanItemsThroughput);
        }
        System.out.println("operations throughput" + operations);
        System.out.println("scans throughput" + scans);
    }

}
