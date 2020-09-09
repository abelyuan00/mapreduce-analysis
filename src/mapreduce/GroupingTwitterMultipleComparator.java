package mapreduce;

import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;

public class GroupingTwitterMultipleComparator extends WritableComparator {
    public GroupingTwitterMultipleComparator() {
        super(TwitterMultiplePair.class,true);
    }

    @Override
    public int compare(WritableComparable a, WritableComparable b) {
        TwitterMultiplePair x = (TwitterMultiplePair)a;
        TwitterMultiplePair y = (TwitterMultiplePair)b;
        return x.getIndex().compareTo(y.getIndex());
    }
}