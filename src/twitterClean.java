import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import entity.TwitterContent;
import mapreduce.MyMultipleCleanPartitioner;
import mapreduce.TwitterMultiplePair;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;



@SuppressWarnings("all")
public class twitterClean {


    // using mapper to output the data 
    static class MultipleSortingMapper extends Mapper<LongWritable, Text, Text, TwitterMultiplePair> {
        private TwitterMultiplePair twitterMultiplePair = new TwitterMultiplePair();

        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        	//Context allows the Mapper/Reducer to interact with the rest of the Hadoop system. 
        	//It includes configuration data for the job as well as interfaces which allow it to emit output.
            TwitterContent twitterContent = parseContent(value);
            //assign value to twitterContent variable
            if (twitterContent == null) {
                return;
            }
            
            twitterMultiplePair.setIndex(twitterContent.getText());
            twitterMultiplePair.setTwitterContent(twitterContent);
            //pass in the twitterContent to multiple pair
            context.write(new Text(twitterContent.getText()), twitterMultiplePair);
            //use context to pass in the pair, key is 
        }
        
        
        
        
/**
 * This method would filter the raw input value and only save the hashtag, username, text and location
 * @param value
 * @return twitterContent
 */
        private TwitterContent parseContent(Text value) {
        	
            TwitterContent twitterContent = new TwitterContent();
            JSONObject contentJson = JSONObject.parseObject(value.toString());
            //pass in json files 
            if (contentJson.containsKey("delete")) {
                return null;
            }

            twitterContent.setText(contentJson.getString("text"));
            //main tweets are contained in the "text" field

            try {
                twitterContent.setCreateTime(new Date(contentJson.getString("created_at")));

                JSONObject entityJson = contentJson.getJSONObject("entities");
                JSONArray tagArray = entityJson.getJSONArray("hashtags");
                //all the hashtags were included in the entities part
                
                List<String> tags = new ArrayList<>();
                if (tagArray.size() > 0) {
                	//using loop to append every hashTag to the string list
                    for (int i = 0; i < tagArray.size(); i++) {
                        tags.add(tagArray.getJSONObject(i).getString("text"));
                    }
                }
                //pass in the looped result to the main variable
                twitterContent.setTagList(tags);
                

                JSONObject userJson = contentJson.getJSONObject("user");
                twitterContent.setLocation(userJson.getString("location"));

            } catch (Exception e) {
                e.printStackTrace();
            }
            return twitterContent;
        }
    }


    static class MultipleSortingReducer extends Reducer<Text, TwitterMultiplePair, Text, Text> {
        @Override
        protected void reduce(Text key, Iterable<TwitterMultiplePair> values, Context context) throws IOException, InterruptedException {
            StringBuffer buffer = new StringBuffer();
            //string buffer could be used as string manipulator, it is a tool to reverse, append and so on
            Iterator<TwitterMultiplePair> iter = values.iterator();
            //iterator can be used in collection looping
            //since the value that about to passed in is collection of username,location etc
            
            TwitterMultiplePair first = iter.next();
            try {
                context.write(new Text(""), new Text(first.getTwitterContent().toString()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void startJob(String input, String output) throws Exception{
        System.out.println("Start clean data ...");
        Path inputPath = new Path(input);
        Path outputPath = new Path(output);

        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf,"TwitterClean");

        FileSystem hdfs = outputPath.getFileSystem(conf);

        //if directory exist then delete it
        if (hdfs.isDirectory(outputPath)){
            hdfs.delete(outputPath,true);
        }


        // output path setting
        FileInputFormat.addInputPath(job,inputPath);
        FileOutputFormat.setOutputPath(job, outputPath);
        job.setJarByClass(twitterClean.class);
        job.setMapperClass(MultipleSortingMapper.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(TwitterMultiplePair.class);

        //set Reducer
        job.setReducerClass(MultipleSortingReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);
        job.setPartitionerClass(MyMultipleCleanPartitioner.class);

//        System.exit(job.waitForCompletion(true)?0:1);

        job.waitForCompletion(true);

        System.out.println("Clean data completed!");

    }

    public static void main(String[] args) throws Exception {
        startJob(args[0], args[1]);
    }

}







