package linearizability;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class HistoryJsonReader{
    public HistoryJsonReader(String dir, int max_cores){
        directory = dir;
        maxCores = max_cores;
    }

    public History read() throws IOException{
        ArrayList<ArrayList<TimedOperation>> concurrent_history = new ArrayList<>();
        for(int i = 0; i < maxCores; ++i){
            Path filepath = Paths.get(directory, Integer.toString(i) + ".json");
            Gson gson = new Gson();
            FileReader file = new FileReader(filepath.toFile());
            JsonReader reader = new JsonReader(file);
            SerializableOperationsList obj = gson.fromJson(reader, SerializableOperationsList.class);
            concurrent_history.add(obj.getSortedList());
        }
        return new History(concurrent_history);
    }

    private String directory;
    private int maxCores;

    public static int getNumThreads(File folder) {
        int numThreads = 0;
        for(File file: folder.listFiles()){
            if(file.getName().endsWith(".json")){
                Integer index = Integer.valueOf(file.getName().replaceFirst(".json", ""));
                numThreads = Math.max(numThreads, index + 1);
            }
        }
        return numThreads;
    }
}
