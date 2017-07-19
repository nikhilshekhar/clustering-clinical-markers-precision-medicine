package seminar.reducer;


import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by nikhilshekhar on 3/26/16.
 */
public class KidneyReducer extends Reducer<Text, Text, Text, NullWritable> {

    ArrayList<String> schema = new ArrayList<>();

    @Override
    public void setup(Context context) throws IOException {
        java.net.URI[] files =context.getCacheFiles();
        Path fileName = new Path(files[0]);

        //Read the file
        BufferedReader br = new BufferedReader(new FileReader(fileName.toString()));
        String[] onlyLine = br.readLine().split(",");
        for(String column : onlyLine){
            schema.add(column);
//            System.out.println("Adding column to the schema in map:"+column);

        }

    }



    protected void reduce(Text key, Iterable<Text> values, Context context)throws IOException, InterruptedException {

//        System.out.println("Key at reducer:"+key);

        String tempKey2 = null;
        boolean inEgfr = false;
        StringBuilder outputValue = new StringBuilder();
//        HashMap<String,HashMap<String,String>> valueMap = new HashMap<String,HashMap<String,String>>();
        HashMap<String,String> tempMap = new HashMap<>();;
        for(Text value: values){
            String split[] = value.toString().split(":");
//            tempMap = new HashMap<>();
            String tempKey = null;
            if (split[0].startsWith("999")){
                inEgfr=true  ;
                tempKey2 = value.toString().substring(3);
                System.out.println("tempKey2:"+tempKey2);
                //Add the columns form egfr value directly to the output
//                outputValue.append(value.toString().substring(3))  ;
//                outputValue.append(",");
                System.out.println("value.toString().substring(3):"+value.toString().substring(3));
//                tempKey = split[0].substring(3);

            }else{
                tempKey = split[0];
                tempMap.put(tempKey,split[1]);
                System.out.println("Adding key to the map:"+tempKey);
                System.out.println("Adding value for the key to the map:"+split[1]);

            }


//            System.out.println("Value:"+value.toString());

        }
//        valueMap.put(key.toString(),tempMap)  ;

        if(inEgfr) {

            outputValue.append(tempKey2)  ;
            outputValue.append(",");
//            System.out.println("value.toString().substring(3):"+tempKey2);

//        Read the output schema and populate the data from the map into it
            for (String column : schema) {
//              String columnValue = tempMap.get(column);
                System.out.println("Column populated:" + column);
                System.out.println("Column populated with value:" + tempMap.get(column));
                outputValue.append(tempMap.get(column));
                System.out.println("The key currently being read from the map:"+column);
                System.out.println("The value of the key read from the map:"+tempMap.get(column));
                outputValue.append(",");
            }
            outputValue.setLength(outputValue.length() - 1);
            String[] keySplit = key.toString().split(":");
//            String newKey = keySplit[0]+","+keySplit[1];
            String val = keySplit[0]+","+keySplit[1]+","+ outputValue.toString();
            System.out.println("Output key being added:"+val);
            System.out.println("Output value being added:"+outputValue.toString());
            tempMap.clear();
            outputValue.setLength(0);
            context.write(new Text(val), NullWritable.get());
        }

    }

    public void cleanup(Context context) throws IOException,InterruptedException {


    }

}




