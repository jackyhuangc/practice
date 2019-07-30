package com.jacky.practice.hadoop;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;


public class FlowSumSort {

    public static class FlowSumSortMapper extends Mapper<LongWritable,Text,FlowBean,Text>{

        Text v = new Text();
        FlowBean k = new FlowBean();

        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException,InterruptedException{
            String line = value.toString();
            String[] fields = line.split("\t");
            String phoneNum = fields[0];

            long upFlow = Long.parseLong(fields[1]);
            long downFlow = Long.parseLong(fields[2]);

            k.set(upFlow,downFlow);
            v.set(phoneNum);

            context.write(k,v);
        }
    }


    public static class FlowSumSortReducer extends Reducer<FlowBean,Text,Text,FlowBean>{
        @Override
        protected void reduce(FlowBean key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            context.write(values.iterator().next(),key);
        }
    }

//    public static void main(String[] args) throws Exception {
//        Configuration conf = new Configuration();
//        Job job = Job.getInstance(conf);
//
//        //指定这个job所在的jar包位置
//        job.setJarByClass(FlowSumSort.class);
//
//        //指定使用的Mapper是哪个类，Reducer是哪个类
//        job.setMapperClass(FlowSumSortMapper.class);
//        job.setReducerClass(FlowSumSortReducer.class);
//
//        //设置业务逻辑Mapper类的输出key 和value的数据类型
//        job.setMapOutputKeyClass(FlowBean.class);
//        job.setMapOutputValueClass(Text.class);
//
//        //设置业务逻辑Reducer类的输出key 和value的数据类型
//        job.setOutputKeyClass(Text.class);
//        job.setOutputValueClass(FlowBean.class);
//
//        FileInputFormat.setInputPaths(job,"D:\\flowsum\\output"); //使用上一次MR的输出结果为输入数据
//        //指定处理完成之后的结果保存位置
//        FileOutputFormat.setOutputPath(job,new Path("D:\\flowsum\\outputsort"));
//
//        //向yarn集群提交这个job
//        boolean res = job.waitForCompletion(true);
//        System.exit(res ? 0 : 1);
//    }
}
