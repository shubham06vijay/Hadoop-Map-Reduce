import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import com.sun.xml.bind.v2.schemagen.xmlschema.List;

import java.io.IOException;
import java.util.*;
import java.util.Scanner;
import java.util.StringTokenizer;

public class q6 {

 // Class to implement the mapper interface
 public static class mvemapper extends Mapper < LongWritable, Text, Text, Text > {
  // Map interface of the MapReduce job
  public void map(LongWritable key, Text value, Context context) throws IOException,
  InterruptedException {
   // Get the current line
   String[] line_temp = value.toString().split("/n");
   int i = 0;
   while (i < line_temp.length) {
    String[] line_values = line_temp[i].split(",");
    if ((line_values[2]).charAt(3) == '2') {
     context.write(new Text("CSL1"), new Text(line_values[1] + " "));
    }
    i++;
   }


  }
 }
 // Class to implement the reducer interface
 public static class mvereducer extends Reducer < Text, Text, Text, Text > {
  public void reduce(Text key, Iterable < Text > values, Context context) throws IOException,
  InterruptedException {
   String ans = "";
   for (Text val: values) {
    ans += val.toString();
   }
   context.write(key, new Text(ans));
  }
 }
 public static void main(String[] args) throws Exception {
  // Check if the arguments are right
  if (args.length != 2) {
   System.err.println("Q2");
   System.exit(-1);
  }

  // Create a job for the mapreduce task
  Job job = new Job();
  job.setJarByClass(q6.class);

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
