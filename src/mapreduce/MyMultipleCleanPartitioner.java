package mapreduce;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Partitioner;

public class MyMultipleCleanPartitioner extends Partitioner<Text, TwitterMultiplePair> {

    @Override
    public int getPartition(Text key, TwitterMultiplePair value, int i) {
        return (key.toString().hashCode() & Integer.MAX_VALUE) % i;
    }

}