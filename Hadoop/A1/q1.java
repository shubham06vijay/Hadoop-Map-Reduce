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

public class q1 {

 // Class to implement the mapper interface
 public static class mvemapper extends Mapper < LongWritable, Text, Text, IntWritable > {
  // Map interface of the MapReduce job
  public void map(LongWritable key, Text value, Context context) throws IOException,
  InterruptedException {
   // Get the current line
   String[] line_temp = value.toString().split("/n");
   int i = 0;
   while (i < line_temp.length) {
    String[] line_values = line_temp[i].split(" ");
    if ((line_values[4]) == "sachin" && (line_values[1]) == "Statistic") {
     IntWritable r = new IntWritable();
     r.set(1);
     context.write(new Text(line_values[4]), r);
    }
    i++;
   }


  }
 }
 // Class to implement the reducer interface
 public static class mvereducer extends Reducer < Text, IntWritable, Text, IntWritable > {
  // Reduce interface of the MapReduce job
  private IntWritable result = new IntWritable();
  public void reduce(Text key, Iterable < IntWritable > values, Context context) throws IOException,
  InterruptedException {
   int sum = 0;
   for (IntWritable val: values) {
    sum += val.get();
   }
   result.set(sum);
   // Write the output
   context.write(key, result);
  }
 }
 public static void main(String[] args) throws Exception {
  // Check if the arguments are right
  if (args.length != 2) {
   System.err.println("Usage - Cricket <input-file> <output-path>");
   System.exit(-1);
  }
  //number of users are watching Sachin Tendulkar Statistic
  // Create a job for the mapreduce task
  Job job = new Job();
  job.setJarByClass(q1.class);

  // Set the input and output path
  FileInputFormat.addInputPath(job, new Path(args[0]));
  FileOutputFormat.setOutputPath(job, new Path(args[1]));

  // set the mapper and reducer class
  job.setMapperClass(mvemapper.class);
  job.setReducerClass(mvereducer.class);

  // Set the key and value class
  job.setOutputKeyClass(Text.class);
  job.setOutputValueClass(IntWritable.class);

  // Wait for the job to finish
  System.exit(job.waitForCompletion(true) ? 0 : 1);
 }
}
