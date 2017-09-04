package linearizability;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import com.google.gson.Gson;


class SerializableOperationsList{
    SerializableOperationsList(ArrayList<TimedOperation> list){
        get = new ArrayList<>();
        getTimes = new ArrayList<>();
        put = new ArrayList<>();
        putTimes = new ArrayList<>();
        discard = new ArrayList<>();
        discardTimes = new ArrayList<>();
        scan = new ArrayList<>();
        scanTimes = new ArrayList<>();
        size = new ArrayList<>();
        sizeTimes = new ArrayList<>();
        sizeLower = new ArrayList<>();
        sizeLowerTimes = new ArrayList<>();
        sizeUpper = new ArrayList<>();
        sizeUpperTimes = new ArrayList<>();
        for(TimedOperation timed: list){
            if(timed.operation.getClass() == Get.class){
                get.add((Get)(timed.operation));
                getTimes.add(timed.interval);
            } else if(timed.operation.getClass() == Put.class){
                put.add((Put)(timed.operation));
                putTimes.add(timed.interval);
            } else if(timed.operation.getClass() == Discard.class){
                discard.add((Discard)(timed.operation));
                discardTimes.add(timed.interval);
            } else if(timed.operation.getClass() == Scan.class){
                scan.add((Scan)(timed.operation));
                scanTimes.add(timed.interval);
            } else if(timed.operation.getClass() == Size.class){
                size.add((Size)(timed.operation));
                sizeTimes.add(timed.interval);
            } else if(timed.operation.getClass() == SizeLowerBound.class){
                sizeLower.add((SizeLowerBound)(timed.operation));
                sizeLowerTimes.add(timed.interval);
            } else if(timed.operation.getClass() == SizeUpperBound.class){
                sizeUpper.add((SizeUpperBound)(timed.operation));
                sizeUpperTimes.add(timed.interval);
            }

        }
    }

    ArrayList<TimedOperation> getSortedList(){
//        System.out.format("#get %d #put %d #discard %d", get.size(), put.size(), discard.size());
        ArrayList<TimedOperation> res = new ArrayList<>();
        for(int i = 0; i < get.size(); ++i){
            res.add(new TimedOperation(get.get(i), getTimes.get(i)));
        }
        for(int i = 0; i < put.size(); ++i){
            res.add(new TimedOperation(put.get(i), putTimes.get(i)));
        }
        for(int i = 0; i < discard.size(); ++i){
            res.add(new TimedOperation(discard.get(i), discardTimes.get(i)));
        }
        for(int i = 0; i < scan.size(); ++i){
            res.add(new TimedOperation(scan.get(i), scanTimes.get(i)));
        }
        for(int i = 0; i < size.size(); ++i){
            res.add(new TimedOperation(size.get(i), sizeTimes.get(i)));
        }
        for(int i = 0; i < sizeLower.size(); ++i){
            res.add(new TimedOperation(sizeLower.get(i), sizeLowerTimes.get(i)));
        }
        for(int i = 0; i < sizeUpper.size(); ++i){
            res.add(new TimedOperation(sizeUpper.get(i), sizeUpperTimes.get(i)));
        }
        Collections.sort(res, TimedOperation.StartTimeComparator);
        return res;
    }

    private ArrayList<Get> get;
    private ArrayList<Interval> getTimes;
    private ArrayList<Put> put;
    private ArrayList<Interval> putTimes;
    private ArrayList<Discard> discard;
    private ArrayList<Interval> discardTimes;
    private ArrayList<Scan> scan;
    private ArrayList<Interval> scanTimes;
    private ArrayList<Size> size;
    private ArrayList<Interval> sizeTimes;
    private ArrayList<SizeLowerBound> sizeLower;
    private ArrayList<Interval> sizeLowerTimes;
    private ArrayList<SizeUpperBound> sizeUpper;
    private ArrayList<Interval> sizeUpperTimes;
}




/**
 * Created by bugabuga on 29/08/17.
 * Writes a Json file for the operations logged by this core.
 */
public class HistoryJsonWriter {
    public HistoryJsonWriter(String dir, int core){
        this.core = core;
        filepath = Paths.get(dir, Integer.toString(core) + ".json");
        operations = new ArrayList<>();
        directory = dir;
    }

    public void addOperation(TimedOperation op){
        operations.add(op);
    }

    public void write() throws IOException{
        if(!Files.exists(Paths.get(directory))){
            Files.createDirectories(Paths.get(directory));
        }
        Gson gson = new Gson();
        try(FileWriter file = new FileWriter(filepath.toString())){
            SerializableOperationsList obj = new SerializableOperationsList(operations);
            gson.toJson(obj, file);
        }  catch (IOException e){
            e.printStackTrace();
        }
    }

    private int core;
    private Path filepath;
    private String directory;
    private ArrayList<TimedOperation> operations;
}


class WriteReadExample {
    public static void main(String[] args) throws IOException {
        String dir = Paths.get(System.getProperty("user.home"),
                "histories", "tmp1").toString();
        for(int i = 0; i < 4; ++i) {
            HistoryJsonWriter writer = new HistoryJsonWriter(dir, i);
            if(i == 0){
                writer.addOperation(new TimedOperation(new Put(10, 20), new Interval(3, 4)));
            }
            writer.addOperation(new TimedOperation(new Get(10, 20), new Interval(5, 20)));

            writer.write();
        }

        HistoryJsonReader reader = new HistoryJsonReader(dir, 4);
        History history = reader.read();
        System.out.println(history.isLinearizable());
    }
}
