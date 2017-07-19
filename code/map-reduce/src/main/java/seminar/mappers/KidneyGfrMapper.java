package seminar.mappers;



import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;

import java.io.IOException;

/**
 * Created by nikhilshekhar on 3/26/16.
 */
public class KidneyGfrMapper extends Mapper<LongWritable, Text, Text, Text> {


    @Override
    public void setup(Context context) throws IOException {

    }


    public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {

        String[] rowElements = value.toString().split(",");
        String outputKey = rowElements[1]+":"+rowElements[2];
        String testNameFormatting = rowElements[3].substring(1,rowElements[3].lastIndexOf("\""));
        String outputValue = "999"+testNameFormatting+","+rowElements[4]+","+rowElements[5]+","+rowElements[6];
        System.out.println("Key:"+outputKey);
        System.out.println("Value:"+outputValue);
        context.write(new Text(outputKey),new Text(outputValue));


//If there are multiple rows for the same (pateintid,timestamp) currently one gets overidden by the other

    }

    /**
     * This method closes the writer on MultipleOutput
     * It also clears the Schema stored in the HashMap.
     *
     * @param context is the Context object needed by the cleanup method
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws InterruptedException the interrupted exception
     */
    public void cleanup(Context context) throws IOException,
            InterruptedException {


    }

}





