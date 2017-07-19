package seminar.driver;

import seminar.mappers.KidneyFindingsMapper;
import seminar.mappers.KidneyGfrMapper;
import seminar.mappers.KidneyLabMapper;
import seminar.reducer.KidneyReducer;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.MultipleInputs;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

/**
 * Created by nikhilshekhar on 3/26/16.
 */
public class KidneyDriver extends Configured implements Tool {
    /**
     * The main method.
     *
     * @param args the arguments
     * @throws Exception the exception
     */
    public static void main(String[] args) throws Exception {

        int returnStatus = ToolRunner.run(new KidneyDriver(), args);
        System.exit(returnStatus);
    }

    @Override
    public int run(String[] args) throws Exception {

        if(args.length < 5) {
            System.out.println(" Usage : hadoop jar <jar name> <input path of GFR data> <input path of Findings data> <input path of Lab data><output path> <fixed header schema file> " );
            System.exit(0);
        }
        
        Configuration conf = getConf();
        Job job = Job.getInstance(conf);

        job.setJobName("Feature matrix generation");
        job.addCacheFile(new Path(args[4]).toUri());

        job.setJarByClass(KidneyDriver.class);
        job.setReducerClass(KidneyReducer.class);
        job.setInputFormatClass(TextInputFormat.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(NullWritable.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(Text.class);
        MultipleInputs.addInputPath(job,new Path(args[0]),TextInputFormat.class,KidneyGfrMapper.class);
        MultipleInputs.addInputPath(job,new Path(args[1]),TextInputFormat.class,KidneyFindingsMapper.class);
        MultipleInputs.addInputPath(job,new Path(args[2]),TextInputFormat.class,KidneyLabMapper.class);
        FileOutputFormat.setOutputPath(job, new Path(args[3]));
//        conf.set("mapreduce.output.textoutputformat.separator", ",");

        boolean success = job.waitForCompletion(true);


        return success ? 0: 1;
    }
}

