import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.conf.Configuration;
import java.io.IOException;
import java.util.*;
import java.util.Scanner;
import java.util.StringTokenizer;

public class q4 {

 // Class to implement the mapper interface
 public static class mvemapper extends Mapper < Object, Text, Text, Text > {
  // Map interface of the MapReduce job
  public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
   String[] line_temp = value.toString().split("/n");
   int i = 0;
   while (i < line_temp.length) {
    String[] line_values = line_temp[i].split(",");
    if (line_values.length > 1 && line_values[1].equals("Statistic")) {
     context.write(new Text(line_values[1]), new Text(line_values[0] + ";" + line_values[4]));
    }
    i++;
   }


  }
 }
 // Class to implement the reducer interface
 public static class mvereducer extends Reducer < Text, Text, Text, Text > {
  // Reduce interface of the MapReduce job
  public void reduce(Text key, Iterable < Text > values, Context context) throws IOException,InterruptedException {
   int sum = 0;
   ArrayList < String > tvShows = new ArrayList < String > ();
   HashMap < String, ArrayList < String >> map = new HashMap < String, ArrayList < String >> ();

   for (Text val: values) {
    String[] str = val.toString().split(";");
    if (!map.containsKey(str[0])) {
     ArrayList < String > arraylist1 = new ArrayList < String > ();
     arraylist1.add(str[1]);
     map.put(str[0], arraylist1); // map.put(temp,map.get(str[0])+tempval);
    } else {
     map.get(str[0]).add(str[1]);
    }
   }

   int no = 0;
   for (Map.Entry < String, ArrayList < String >> en: map.entrySet()) {
    int c = 0;
    tvShows = en.getValue();
    Iterator < String > tvShowIterator = tvShows.iterator();
    while (tvShowIterator.hasNext()) {
     String tvShow = tvShowIterator.next();
     if (tvShow.equals("sachin tendulkar") || tvShow.equals("V Sehwag"))
      c++;
    }
    if (c >= 2) {
     no++;
    }
   }
   context.write(new Text(""), new Text(" " + no));
  }
 }
 public static void main(String[] args) throws Exception {

  // Create a job for the mapreduce task
  Configuration conf = new Configuration();
  Job job = Job.getInstance(conf, "How many users are watching statistic of VVS Laxman and Virendra Sehwag simultaneously?");
  job.setJarByClass(q4.class);

  // Set the input and output path
  FileInputFormat.addInputPath(job, new Path(args[0]));
  FileOutputFormat.setOutputPath(job, new Path(args[1]));

  // set the mapper and reducer class
  job.setMapperClass(mvemapper.class);
  job.setReducerClass(mvereducer.class);

  // Set the key and value class
  job.setOutputKeyClass(Text.class);
  job.setOutputValueClass(Text.class);

  // Wait for the job to finish
  System.exit(job.waitForCompletion(true) ? 0 : 1);
 }
}
