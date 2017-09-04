package linearizability;

import java.util.Comparator;

/**
 * Created by bugabuga on 27/08/17.
 */
public class TimedOperation {
    public MapOperation operation;
    Interval interval;

    public TimedOperation(MapOperation operation) {
        this.operation = operation;
        interval = new Interval();
    }

    public void setEnd(){
        interval.setEnd();
    }

    TimedOperation(MapOperation operation, Interval interval) {
        this.operation = operation;
        this.interval = interval;
    }

    public static Comparator<TimedOperation> StartTimeComparator = new Comparator<TimedOperation>() {
        @Override
        public int compare(TimedOperation o1, TimedOperation o2) {
            return Double.compare(o1.interval.start, o2.interval.start);
        }
    };
}
