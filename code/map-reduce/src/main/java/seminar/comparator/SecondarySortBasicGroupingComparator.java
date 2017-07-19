package seminar.comparator;

import seminar.customwritable.CompositeKeyWritable;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;

/**
 * Created by nikhilshekhar on 4/19/16.
 */


public class SecondarySortBasicGroupingComparator extends WritableComparator {
    protected SecondarySortBasicGroupingComparator() {
        super(CompositeKeyWritable.class, true);
    }

    @Override
    public int compare(WritableComparable w1, WritableComparable w2) {
        CompositeKeyWritable key1 = (CompositeKeyWritable) w1;
        CompositeKeyWritable key2 = (CompositeKeyWritable) w2;
        return key1.getkey1().compareTo(key2.getkey1());
    }
}