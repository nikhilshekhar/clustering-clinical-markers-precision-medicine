package seminar.reducer;

import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import seminar.customwritable.CompositeKeyWritable;

import java.io.IOException;
import java.util.LinkedHashMap;

/**
 * Created by nikhilshekhar on 3/26/16.
 */
public class SortOnTimeStampReducer extends Reducer<CompositeKeyWritable, Text, Text, NullWritable> {


    @Override
    public void setup(Context context) throws IOException {

    }

    protected void reduce(CompositeKeyWritable key, Iterable<Text> values, Context context)throws IOException, InterruptedException {
        LinkedHashMap<String,String> rowsOfOnePatient = new LinkedHashMap<String,String>();
            for(Text value: values){
                rowsOfOnePatient.put(key.toString(),value.toString());
//                context.write(value,NullWritable.get());
            }

        System.out.println(rowsOfOnePatient.size());
        if(rowsOfOnePatient.size()>=9){
            for(String keyFromMap : rowsOfOnePatient.keySet()){
                System.out.println("Writing");
                context.write(new Text(rowsOfOnePatient.get(keyFromMap)),NullWritable.get());
            }

        }else{
            System.out.println("Skipping");
        }
        rowsOfOnePatient.clear();


    }

    public void cleanup(Context context) throws IOException,InterruptedException {


    }

}

