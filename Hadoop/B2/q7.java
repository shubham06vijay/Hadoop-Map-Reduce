import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.MultipleInputs;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class q7 {
 public static class CustsMapper extends Mapper < Object, Text, Text, Text > {
  public void map(Object key, Text value, Context context) throws IOException,InterruptedException {
   String record = value.toString();
   String[] parts = record.split(",");
   context.write(new Text(parts[1]), new Text("tab1\t" + parts[0]));
  }
 }

 public static class TxnsMapper extends Mapper < Object, Text, Text, Text > {
  public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
   String record = value.toString();
   String[] parts = record.split(",");
   context.write(new Text(parts[1]), new Text("tab2\t" + parts[0]));
  }
 }

 public static class ReduceJoinReducer extends Reducer < Text, Text, Text, Text > {
  public void reduce(Text key, Iterable < Text > values, Context context) throws IOException,InterruptedException {
   int flag = 0;
   String roomno="";
   for (Text t: values) {
    String parts[] = t.toString().split("\t");
    if (parts[0].equals("tab1")) {
     if (parts[1].equals("2"))
      flag = 1;
    } else if (parts[0].equals("tab2")) {
     roomno = parts[1];
    }
   }

   if (flag == 1)
    context.write(new Text(roomno), new Text("is the room no"));
  }
 }

 public static void main(String[] args) throws Exception {
  Configuration conf = new Configuration();
  Job job = new Job(conf, "Display hostel room no. for student id=2");

  job.setJarByClass(q7.class);

  job.setReducerClass(ReduceJoinReducer.class);

  job.setOutputKeyClass(Text.class);
  job.setOutputValueClass(Text.class);

  MultipleInputs.addInputPath(job, new Path(args[0]), TextInputFormat.class, CustsMapper.class);
  MultipleInputs.addInputPath(job, new Path(args[1]), TextInputFormat.class, TxnsMapper.class);
  Path outputPath = new Path(args[2]);

  FileOutputFormat.setOutputPath(job, outputPath);
  outputPath.getFileSystem(conf).delete(outputPath);
  System.exit(job.waitForCompletion(true) ? 0 : 1);
 }
}
