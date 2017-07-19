package seminar.customwritable;

import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableUtils;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by nikhilshekhar on 4/19/16.
 */
public class CompositeKeyWritable implements Writable,
        WritableComparable<CompositeKeyWritable> {

    private String key1;
    private String key2;

    public CompositeKeyWritable() {
    }

    public CompositeKeyWritable(String key1, String key2) {
        this.key1 = key1;
        this.key2 = key2;
    }

    @Override
    public String toString() {
        return (new StringBuilder().append(key1).append("\t")
                .append(key2)).toString();
    }

    public void readFields(DataInput dataInput) throws IOException {
        key1 = WritableUtils.readString(dataInput);
        key2 = WritableUtils.readString(dataInput);
    }

    public void write(DataOutput dataOutput) throws IOException {
        WritableUtils.writeString(dataOutput, key1);
        WritableUtils.writeString(dataOutput, key2);
    }

    public int compareTo(CompositeKeyWritable objKeyPair) {
        // TODO:
		/*
		 * Note: This code will work as it stands; but when CompositeKeyWritable
		 * is used as key in a map-reduce program, it is de-serialized into an
		 * object for comapareTo() method to be invoked;
		 *
		 * To do: To optimize for speed, implement a raw comparator - will
		 * support comparison of serialized representations
		 */
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 = null,date2 = null;
        try {
            date1 = sdf.parse(key2);
            date2 = sdf.parse(objKeyPair.key2);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        int result = key1.compareTo(objKeyPair.key1);
        if (0 == result) {
            result = date1.compareTo(date2);
        }
        return result;

    }

    public String getkey1() {
        return key1;
    }

    public void setkey1(String key1) {
        this.key1 = key1;
    }

    public String getkey2() {
        return key2;
    }

    public void setkey2(String key2) {
        this.key2 = key2;
    }
}
