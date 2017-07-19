package seminar.partitioner;

import org.apache.hadoop.io.Text;
import seminar.customwritable.CompositeKeyWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Partitioner;

/**
 * Created by nikhilshekhar on 4/19/16.
 */
public class SecondarySortBasicPartitioner extends
        Partitioner<CompositeKeyWritable, Text> {

    @Override
    public int getPartition(CompositeKeyWritable key, Text value,
                            int numReduceTasks) {

        return ((key.getkey1().hashCode() & Integer.MAX_VALUE) % numReduceTasks);
    }
}


