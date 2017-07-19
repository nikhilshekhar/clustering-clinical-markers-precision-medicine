package seminar.comparator;

import seminar.customwritable.CompositeKeyWritable;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by nikhilshekhar on 4/19/16.
 */


public class SecondarySortBasicCompKeySortComparator extends WritableComparator {

    protected SecondarySortBasicCompKeySortComparator() {
        super(CompositeKeyWritable.class, true);
    }

    @Override
    public int compare(WritableComparable w1, WritableComparable w2) {
        CompositeKeyWritable key1 = (CompositeKeyWritable) w1;
        CompositeKeyWritable key2 = (CompositeKeyWritable) w2;


        int cmpResult = key1.getkey1().compareTo(key2.getkey1());
        if (cmpResult == 0)// same deptNo
        {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date date1 = null,date2 = null;
            try {
                System.out.println("Inside parse:"+((CompositeKeyWritable) w1).getkey2()+":key2:"+((CompositeKeyWritable) w2).getkey2());
                date1 = sdf.parse(((CompositeKeyWritable) w1).getkey2());
                System.out.println("Parsed date 1");
                date2 = sdf.parse(((CompositeKeyWritable) w2).getkey2());
                System.out.println("Parsed date 2");

            } catch (ParseException e) {
                e.printStackTrace();
            }
            return date1
                    .compareTo(date2);
            //If the minus is taken out, the values will be in
            //ascending order
        }
        return cmpResult;
    }
}