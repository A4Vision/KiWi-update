package linearizability;

/**
 * Created by bugabuga on 27/08/17.
 */


class Interval {
    double start;
    double end;

    private static long appStart = System.nanoTime();

    private double now(){
        return (double)(System.nanoTime() - appStart);
    }

    Interval(){
        start = now();
        end = 0;
    }

    Interval(double start, double end) {
        this.start = start;
        this.end = end;
    }

    void setEnd(){
        end = now();
    }

    public String toString(){
        return String.format("I(%.1f, %.1ff)", start, end);
    }
}
