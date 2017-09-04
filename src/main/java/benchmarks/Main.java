package benchmarks;

/**
 * Created by gum on 8/21/2017.
 */
public class Main {

    public static void main(String[] args) {
        if (args.length == 0) {
            runAllTests(1000000,
                    1000000,
                    "both");
        } else {
            int itemsRange = Integer.parseInt(args[0]);
            int operationsNumber = Integer.parseInt(args[1]);
            runAllTests(itemsRange, operationsNumber, args[2]);
        }
    }

    private static void runAllTests(int itemsRange,
                                    int operationsNumber,
                                    String mapType) {
        System.out.println("running tests with items range " + itemsRange +
            ", " +operationsNumber + " operations ");
        MapsComparator comparator = new MapsComparator(itemsRange,
                operationsNumber);
        comparator.preFillMaps();
        comparator.initThreads();
        if (mapType.equals("both")) {
            System.out.println("hashmap operations took: " + comparator.performHashMapOperations()
                    + " millisocends");
            System.out.println("kiwi operations took: " + comparator.performKiwiOperations() +
                    " milliseconds");
        } else if (mapType.equals("kiwi")) {
            System.out.println("kiwi operations took: " + comparator.performKiwiOperations() +
                    " milliseconds");
        } else if (mapType.equals("hash")) {
            System.out.println("hashmap operations took: " + comparator.performHashMapOperations()
                    + " millisocends");
        } else {
            System.out.println("please insert valid test type:{hash/kiwi/both}");
        }
    }


}
