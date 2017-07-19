import org.apache.avro.JsonProperties;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.MultipleInputs;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by nikhilshekhar on 3/26/16.
 */
public class KindeyDriver extends Configured implements Tool {
    /**
     * The main method.
     *
     * @param args the arguments
     * @throws Exception the exception
     */
    public static void main(String[] args) throws Exception {

        int returnStatus = ToolRunner.run(new KindeyDriver(), args);
        System.exit(returnStatus);
    }

    @Override
    public int run(String[] args) throws Exception {

        if(args.length < 5) {
            System.out.println(" Usage : hadoop jar <jar name> <input path of GFR data> <input path of Findings data> <input path of Lab data><output path> <fixed header schema file> " );
            System.exit(0);
        }

//        Job job = new Job(getConf());
        Configuration conf = getConf();
        Job job = Job.getInstance(conf);


        job.setJobName("Feature matrix generation");
        job.addCacheFile(new Path(args[4]).toUri());

        job.setJarByClass(KindeyDriver.class);
        job.setMapperClass(KidneyGfrMapper.class);
        job.setReducerClass(KidneyReducer.class);
        job.setInputFormatClass(TextInputFormat.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(NullWritable.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(Text.class);
        MultipleInputs.addInputPath(job,new Path(args[0]),TextInputFormat.class,KidneyGfrMapper.class);
        MultipleInputs.addInputPath(job,new Path(args[1]),TextInputFormat.class,KidneyFindingsMapper.class);
        MultipleInputs.addInputPath(job,new Path(args[2]),TextInputFormat.class,KidneyLabMapper.class);
//        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[3]));
//        conf.set("mapreduce.output.textoutputformat.separator", ",");

        boolean success = job.waitForCompletion(true);

//        AmexLogger _logger = new AmexLogger(job.getJobID().toString(), "Driver", this.getClass().toString(), args[5], FileSystem.get(job.getConfiguration()), LogInfoLevel.Brief);
//
//        long numberOfHeaderTrailer = job.getCounters().findCounter(ValidationMapper.MapperCounters.HEADERANDTRAILER).getValue();
//        long recordsToBeProcessed = job.getCounters().findCounter(ValidationMapper.MapperCounters.RECORDSTOBEPROCESSED).getValue();
//        long recordsOfInterest = job.getCounters().findCounter(ValidationMapper.MapperCounters.RECORDOFINTEREST).getValue();
//        long recordsNotOfInterest = job.getCounters().findCounter(ValidationMapper.MapperCounters.RECORDNOTOFINTEREST).getValue();
//        _logger.write(LogLevel.INFO, "********Mapper Counters*********");
//        _logger.write(LogLevel.INFO, "Number of headers and trailers in input file:"+numberOfHeaderTrailer);
//        _logger.write(LogLevel.INFO, "Number of input records to be processed excluding the header and trailers:"+recordsToBeProcessed);
//        _logger.write(LogLevel.INFO, "Number of records of type 010,011,030,031:"+recordsOfInterest);
//        _logger.write(LogLevel.INFO, "Number of records excluding the ones of type 010,011,030,031:"+recordsNotOfInterest);
//
//
//        long reduceOutputRecords = job.getCounters().findCounter(ValidationReducer.ReducerCounters.REDUCEOUTPUTRECORDS).getValue();
//        long badSourceCodes = job.getCounters().findCounter(ValidationReducer.ReducerCounters.BADSOURCECODES).getValue();
//        long badNumericValues = job.getCounters().findCounter(ValidationReducer.ReducerCounters.BADNUMERICVLAUES).getValue();
//        long sourceCodeMappingNotFound = job.getCounters().findCounter(ValidationReducer.ReducerCounters.SOURCECODEMAPPINGNOTFOUND).getValue();
//        long zcodeError = job.getCounters().findCounter(ValidationReducer.ReducerCounters.ZCODEERROR).getValue();
//        _logger.write(LogLevel.INFO, "********Reducer Counters*********");
//        _logger.write(LogLevel.INFO, "Number of reduce output records:"+reduceOutputRecords);
//        _logger.write(LogLevel.INFO, "Number of bad source codes in the input files, source code with length less than 5:"+badSourceCodes);
//        _logger.write(LogLevel.INFO, "Number of bad numeric values in the input file:"+badNumericValues);
//        _logger.write(LogLevel.INFO, "Source code for mapping was not found:"+sourceCodeMappingNotFound);



        return success ? 0: 1;
    }
}
