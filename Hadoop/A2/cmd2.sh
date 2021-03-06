#!/bin/sh
hadoop fs -rm -r -f /top5Scorers

hadoop fs -mkdir /top5Scorers

hadoop fs -mkdir /top5Scorers/Input

hadoop fs -put '/home/hadoop/Desktop/DSAssg/A2/input_data/input.txt' /top5Scorers/Input

javac -classpath ${HADOOP_CLASSPATH} -d '/home/hadoop/Desktop/DSAssg/A2/Classes' '/home/hadoop/Desktop/DSAssg/A2/q2.java' 

jar -cvf firstTut.jar -C '/home/hadoop/Desktop/DSAssg/A2/Classes' .

hadoop jar '/home/hadoop/Desktop/DSAssg/A2/firstTut.jar' q2 /top5Scorers/Input /top5Scorers/Output

hadoop dfs -cat /top5Scorers/Output/*
