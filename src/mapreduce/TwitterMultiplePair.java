package mapreduce;

import com.alibaba.fastjson.JSON;
import entity.TwitterContent;
import org.apache.hadoop.io.WritableComparable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Base64;

public class TwitterMultiplePair implements WritableComparable<TwitterMultiplePair> {
    private String index;
    private TwitterContent twitterContent;
    
    public TwitterMultiplePair(String index, TwitterContent twitterContent) {
        this.index = index;
        this.twitterContent = twitterContent;
    }


    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public TwitterContent getTwitterContent() {
        return twitterContent;
    }

    public void setTwitterContent(TwitterContent twitterContent) {
        this.twitterContent = twitterContent;
    }

    public TwitterMultiplePair() {
    }

    @Override
    public String toString() {
        return index + " > " + twitterContent;
    }



    @Override
    public int compareTo(TwitterMultiplePair twitterMultiplePair) {
        return Long.compare(this.getTwitterContent().getCreateTime().getTime(), twitterMultiplePair.getTwitterContent().getCreateTime().getTime());
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        dataOutput.writeUTF(this.index);
        dataOutput.writeBytes(Base64.getEncoder().encodeToString(this.twitterContent.toString().getBytes()));
    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {
        this.index = dataInput.readUTF();
        String date = new String(Base64.getDecoder().decode(dataInput.readLine()));
        this.twitterContent = JSON.parseObject(date, TwitterContent.class);
    }

}
