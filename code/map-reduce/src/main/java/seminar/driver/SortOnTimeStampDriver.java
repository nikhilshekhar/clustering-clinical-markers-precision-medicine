package seminar.driver;

import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import seminar.customwritable.CompositeKeyWritable;
import seminar.comparator.SecondarySortBasicCompKeySortComparator;
import seminar.comparator.SecondarySortBasicGroupingComparator;
import seminar.mappers.SortOnTimeStampMapper;
import seminar.partitioner.SecondarySortBasicPartitioner;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import seminar.reducer.SortOnTimeStampReducer;

/**
 * Created by nikhilshekhar on 4/19/16.
 */

public class SortOnTimeStampDriver extends Configured implements Tool {
    /**
     * The main method.
     *
     * @param args the arguments
     * @throws Exception the exception
     */
    public static void main(String[] args) throws Exception {

        int returnStatus = ToolRunner.run(new SortOnTimeStampDriver(), args);
        System.exit(returnStatus);
    }

    @Override
    public int run(String[] args) throws Exception {

        if(args.length < 2) {
            System.out.println(" Usage : hadoop jar <jar name> <input path of data file><output path> " );
            System.exit(0);
        }

        Configuration conf = getConf();
        Job job = Job.getInstance(conf);
        job.setJobName("Sorting on timestamp");
        job.setJarByClass(SortOnTimeStampDriver.class);
        job.setMapperClass(SortOnTimeStampMapper.class);
        job.setReducerClass(SortOnTimeStampReducer.class);
        job.setNumReduceTasks(1);
        job.setInputFormatClass(TextInputFormat.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(NullWritable.class);
        job.setMapOutputKeyClass(CompositeKeyWritable.class);
        job.setMapOutputValueClass(Text.class);
        job.setPartitionerClass(SecondarySortBasicPartitioner.class);
        job.setSortComparatorClass(SecondarySortBasicCompKeySortComparator.class);
        job.setGroupingComparatorClass(SecondarySortBasicGroupingComparator.class);
        FileInputFormat.setInputPaths(job,new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        boolean success = job.waitForCompletion(true);

        return success ? 0: 1;
    }
}


