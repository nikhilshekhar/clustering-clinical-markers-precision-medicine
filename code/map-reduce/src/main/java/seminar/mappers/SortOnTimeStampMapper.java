package seminar.mappers;

import seminar.customwritable.CompositeKeyWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

/**
 * Created by nikhilshekhar on 4/19/16.
 */


public class SortOnTimeStampMapper extends Mapper<LongWritable, Text, CompositeKeyWritable, Text> {


    @Override
    public void setup(Context context) throws IOException {

    }


    public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {

        String[] rowElements = value.toString().split(",");

//        String outputKey = rowElements[1]+":"+rowElements[2];
//        if (rowElements[1].compareTo("8545333")==0) {
            String testNameFormatting = rowElements[3].substring(1, rowElements[3].lastIndexOf("\""));
            String outputValue = testNameFormatting + ":" + rowElements[4];
            String key2 = rowElements[2].substring(1);
            String key3 = key2.substring(0,key2.length()-1);
            System.out.println("Key1:"+rowElements[1]);
            System.out.println("Key2:"+rowElements[2]);
            System.out.println("Key3:"+key3);
//            System.out.println("Key:" + rowElements[1]+":"+key3);
            System.out.println("Value:" + outputValue);

            context.write(new CompositeKeyWritable(rowElements[1],key3), value);
//        }


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
