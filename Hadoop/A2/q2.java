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

public class q2 {
 public static class mvemapper extends Mapper < Object, Text, Text, Text > {
  public void map(Object key, Text value, Context context) throws IOException,InterruptedException {
   String[] line_temp = value.toString().split("/n");
   int i = 0;
   while (i < line_temp.length) {
    String[] line_values = line_temp[i].split(",");
    if (line_values.length > 1 && line_values[5].equals("TEST")) {
     context.write(new Text(line_values[5]), new Text(line_values[3] + ";" + line_values[7])); //3 has Player_ID and 7 has runs
    }
    i++;
   }
  }
 }
 // Class to implement the reducer interface
 public static class mvereducer extends Reducer < Text, Text, Text, Text > {


  // Sort the HASHMAP
  public static HashMap < String,  Integer > sortByValue(HashMap < String, Integer > hm) {
   // Create a list from elements of HashMap
   List < Map.Entry < String, Integer > > list = new LinkedList < Map.Entry < String, Integer > > (hm.entrySet());
   // Sort the list
   Collections.sort(list, new Comparator < Map.Entry < String, Integer > > () {
    public int compare(Map.Entry < String, Integer > o1, Map.Entry < String, Integer > o2) {
     return -((o1.getValue()).compareTo(o2.getValue()));
    }
   });
   // put data from sorted list to hashmap
   HashMap < String, Integer > temp = new LinkedHashMap < String, Integer > ();
   for (Map.Entry < String, Integer > aa: list) {
    temp.put(aa.getKey(), aa.getValue());
   }
   return temp;
  }


  public void reduce(Text key, Iterable < Text > values, Context context) throws IOException,  InterruptedException {
   int sum = 0;
   HashMap < String, Integer > map = new HashMap < String, Integer > ();
   int tempval = 0;
   for (Text val: values) {
    String[] str = val.toString().split(";");
    tempval = Integer.parseInt(str[1]);
    if (!map.containsKey(str[0])) {
     map.put(str[0], tempval);
    }
   }
   Map < String, Integer > hm = sortByValue(map);
   int i = 0;
   for (Map.Entry < String, Integer > en: hm.entrySet()) {
    if (i < 5) {
     context.write(new Text(en.getKey()), new Text(en.getValue().toString()));
    }
    i++;
   }
  }
 }
 public static void main(String[] args) throws Exception {
  Configuration conf = new Configuration();
  Job job = Job.getInstance(conf, "first 5 most run scorer in Test Matches.");
  job.setJarByClass(q2.class);

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
