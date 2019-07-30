package com.jacky.practice;

import com.jacky.practice.hadoop.*;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.hdfs.DistributedFileSystem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;


import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class T_Hadoop extends Configured {
    public static void main(String[] args) throws Exception {
        //new T_Hadoop().test1();
        new T_Hadoop().test2();
        new T_Hadoop().test3();
        new T_Hadoop().test4();
        //test5();
        //test6();
        test7();
    }

    public void test1() {
        try {
            Configuration conf = new Configuration();

            // 不设置该代码会出现错误：java.io.IOException: No FileSystem for scheme: hdfs
            conf.set("fs.hdfs.impl", "org.apache.hadoop.hdfs.DistributedFileSystem");

            // 如果通过非HA模式访问，只能访问leader节点
            String filePath = "hdfs://slave1:9000/jacky/hello1.txt";

            Path path = new Path(filePath);

            // 这里需要设置URI，否则出现错误：java.lang.IllegalArgumentException: Wrong FS: hdfs://127.0.0.1:9000/test/test.txt, expected: file:///
            FileSystem fs = FileSystem.get(new URI(filePath), conf, "root");

            if (!fs.exists(path)) {

                fs.createNewFile(path);
            }

            System.out.println("READING ============================");
            FSDataInputStream is = fs.open(path);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            // 示例仅读取一行
            String content = br.readLine();
            System.out.println(content);

            // 再读二行
            content = br.readLine();
            System.out.println(content);
            content = br.readLine();
            System.out.println(content);
            br.close();

            System.out.println("WRITING ============================");
            byte[] buff = "this is helloworld from java api!\n".getBytes();
            FSDataOutputStream os = fs.create(path);
            os.write(buff, 0, buff.length);
            os.close();

            fs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void test2() {
        try {
//            Configuration conf = new Configuration();
//
//            // 不设置该代码会出现错误：java.io.IOException: No FileSystem for scheme: hdfs
//            conf.set("fs.hdfs.impl", "org.apache.hadoop.hdfs.DistributedFileSystem");
//
//            String filePath = "hdfs://master:9000/jacky/hello1.txt";


            Configuration conf = new Configuration();
            conf.set("fs.defaultFS", "hdfs://ns1");
            conf.set("dfs.nameservices", "ns1");
            conf.set("dfs.ha.namenodes.ns1", "nn1,nn2");
            conf.set("dfs.namenode.rpc-address.ns1.nn1", "master:9000");
            conf.set("dfs.namenode.rpc-address.ns1.nn2", "slave1:9000");
//            //conf.setBoolean(name, value);
            conf.set("dfs.client.failover.proxy.provider.ns1", "org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider");


            //conf.set("fs.hdfs.impl", "org.apache.hadoop.hdfs.DistributedFileSystem");
            //FileSystem fs = FileSystem.get(new URI("hdfs://ns1"), conf, "hadoop");

            String filePath = "hdfs://ns1";
            Path path = new Path(filePath);

            //String hdfsRPCUrl = "hdfs://master:" + 9000;
            DistributedFileSystem dfs = new DistributedFileSystem();
            try {
                dfs.initialize(URI.create(filePath), conf);
                Path tmpPath2 = new Path("/tmp2");
                dfs.mkdir(tmpPath2, new FsPermission("777"));
                FileStatus[] list = dfs.listStatus(new Path("/"));
                for (FileStatus file : list) {
                    System.out.println(file.getPath());
                }
                dfs.setQuota(tmpPath2, 100, 1000);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    dfs.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

//            // 这里需要设置URI，否则出现错误：java.lang.IllegalArgumentException: Wrong FS: hdfs://127.0.0.1:9000/test/test.txt, expected: file:///
//            FileSystem fs = FileSystem.get(new URI(filePath), conf, "root");
//
//            if (!fs.exists(path)) {
//
//                fs.createNewFile(path);
//            }
//
//            System.out.println("READING ============================");
//            FSDataInputStream is = fs.open(path);
//            BufferedReader br = new BufferedReader(new InputStreamReader(is));
//            // 示例仅读取一行
//            String content = br.readLine();
//            System.out.println(content);
//
//            // 再读二行
//            content = br.readLine();
//            System.out.println(content);
//            content = br.readLine();
//            System.out.println(content);
//            br.close();
//
//            System.out.println("WRITING ============================");
//            byte[] buff = "this is helloworld from java api!\n".getBytes();
//            FSDataOutputStream os = fs.create(path);
//            os.write(buff, 0, buff.length);
//            os.close();
//
//            fs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void test3() {
        try {
            //Configuration conf = new Configuration();

            // 不设置该代码会出现错误：java.io.IOException: No FileSystem for scheme: hdfs
            //conf.set("fs.hdfs.impl", "org.apache.hadoop.hdfs.DistributedFileSystem");


            Configuration conf = new Configuration();
            conf.set("fs.defaultFS", "hdfs://ns1");
            conf.set("dfs.nameservices", "ns1");
            conf.set("dfs.ha.namenodes.ns1", "nn1,nn2");
            conf.set("dfs.namenode.rpc-address.ns1.nn1", "master:9000");
            conf.set("dfs.namenode.rpc-address.ns1.nn2", "slave1:9000");
//            //conf.setBoolean(name, value);
            conf.set("dfs.client.failover.proxy.provider.ns1", "org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider");


            //conf.set("fs.hdfs.impl", "org.apache.hadoop.hdfs.DistributedFileSystem");
            //FileSystem fs = FileSystem.get(new URI("hdfs://ns1"), conf, "hadoop");

            //String filePath = "hdfs://master:9000/jacky/hello1.txt";
            String filePath = "hdfs://ns1";

            Path path = new Path(filePath);

            FileSystem fs = FileSystem.get(new URI(filePath), conf);


            DistributedFileSystem dfs = new DistributedFileSystem();
            dfs.initialize(URI.create(filePath), conf);
            Path tmpPath2 = new Path("/jacky/hello1.txt");
            if (!dfs.exists(tmpPath2)) {
                //dfs.mkdir(tmpPath2, new FsPermission("777"));
                dfs.create(tmpPath2);
            } else {
                System.out.println("=======文件已存在==========");
            }

//            FileSystem fs = null;
//            if (dfs.getChildFileSystems().length > 0) {
//                fs = dfs.getChildFileSystems()[0];
//            } else {
//                System.out.println("FileSystem为空 ============================");
//            }
//
//            if (!fs.exists(path)) {
//
//                fs.createNewFile(path);
//            }

            System.out.println("READING ============================");
            FSDataInputStream is = dfs.open(tmpPath2);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            // 示例仅读取一行
            String content = br.readLine();
            System.out.println(content);

            // 再读二行
            content = br.readLine();
            System.out.println(content);
            content = br.readLine();
            System.out.println(content);
            br.close();

            System.out.println("WRITING ============================");
            byte[] buff = "this is helloworld from java api!\n".getBytes();
            FSDataOutputStream os = dfs.create(tmpPath2);
            os.write(buff, 0, buff.length);
            os.close();

            dfs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void test4() {
        try {
            Configuration conf = new Configuration();
            conf.set("fs.defaultFS", "hdfs://ns1");
            conf.set("dfs.nameservices", "ns1");
            conf.set("dfs.ha.namenodes.ns1", "nn1,nn2");
            conf.set("dfs.namenode.rpc-address.ns1.nn1", "master:9000");
            conf.set("dfs.namenode.rpc-address.ns1.nn2", "slave1:9000");
//            //conf.setBoolean(name, value);
            conf.set("dfs.client.failover.proxy.provider.ns1", "org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider");


            //conf.set("fs.hdfs.impl", "org.apache.hadoop.hdfs.DistributedFileSystem");
            //FileSystem fs = FileSystem.get(new URI("hdfs://ns1"), conf, "hadoop");

            //String filePath = "hdfs://master:9000/jacky/hello1.txt";
            // 这是具体的文件名
            String filePath = "hdfs://ns1/jacky/hello1.txt";

            Path path = new Path(filePath);

            FileSystem fs = FileSystem.get(new URI(filePath), conf);


            if (!fs.exists(path)) {

                fs.createNewFile(path);
            }

            System.out.println("READING ============================");
            FSDataInputStream is = fs.open(path);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            // 示例仅读取一行
            String content = br.readLine();
            System.out.println(content);

            // 再读二行
            content = br.readLine();
            System.out.println(content);
            content = br.readLine();
            System.out.println(content);
            br.close();

            System.out.println("WRITING ============================");
            byte[] buff = "this is helloworld from java api!\n".getBytes();
            FSDataOutputStream os = fs.create(path);
            os.write(buff, 0, buff.length);
            os.close();

            fs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void test5() throws Exception {
        //通过Job来封装本次mr的相关信息

        Configuration conf = new Configuration();
        conf.set("fs.defaultFS", "hdfs://ns1");
        conf.set("dfs.nameservices", "ns1");
        conf.set("dfs.ha.namenodes.ns1", "nn1,nn2");
        conf.set("dfs.namenode.rpc-address.ns1.nn1", "master:9000");
        conf.set("dfs.namenode.rpc-address.ns1.nn2", "slave1:9000");
//            //conf.setBoolean(name, value);
        conf.set("dfs.client.failover.proxy.provider.ns1", "org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider");

        // 即使没有下面这行,也可以本地运行 因\hadoop-mapreduce-client-core-2.7.4.jar!\mapred-default.xml 中默认的参数就是 local
        //conf.set("mapreduce.framework.name","local");
        Job job = Job.getInstance(conf);

        //指定本次mr job jar包运行主类
        job.setJarByClass(T_Hadoop.class);

        //指定本次mr 所用的mapper reducer类分别是什么
        job.setMapperClass(WordCountMapper.class);
        job.setReducerClass(WordCountReducer.class);

        //指定本次mr mapper阶段的输出  k  v类型
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(IntWritable.class);

        //指定本次mr 最终输出的 k v类型
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        // job.setNumReduceTasks(3); //ReduceTask个数

        //如果业务有需求，就可以设置combiner组件
        job.setCombinerClass(WordCountReducer.class);

        //指定本次mr 输入的数据路径 和最终输出结果存放在什么位置
        //FileInputFormat.setInputPaths(job, "/Users/huangchao/docker/input");
        //FileOutputFormat.setOutputPath(job, new Path("/Users/huangchao/docker/output"));
        //如果出现0644错误或找不到winutils.exe,则需要设置windows环境和相关文件.

        //上面的路径是本地测试时使用，如果要打包jar到hdfs上运行时，需要使用下面的路径。
        FileInputFormat.setInputPaths(job, new Path("hdfs://ns1/wordcount/input"));
        FileSystem fs = DistributedFileSystem.get(new URI("hdfs://ns1"), conf);
        if (fs.exists(new Path("hdfs://ns1/wordcount/output"))) {
            System.out.println("output文件已经存在，正在删除...");
            fs.delete(new Path("hdfs://ns1/wordcount/output"), true);
        }

        FileOutputFormat.setOutputPath(job, new Path("hdfs://ns1/wordcount/output"));


        /*** 这是使用普通模式访问hadoop***/
//        FileInputFormat.setInputPaths(job, new Path("hdfs://master:9000/wordcount/input"));
//        FileSystem fs = DistributedFileSystem.get(new URI("hdfs://master:9000"), conf);
//        if (fs.exists(new Path("hdfs://master:9000/wordcount/output"))) {
//            System.out.println("output文件已经存在，正在删除...");
//            fs.delete(new Path("hdfs://master:9000/wordcount/output"), true);
//        }
//
//        FileOutputFormat.setOutputPath(job, new Path("hdfs://master:9000/wordcount/output"));
//

        // job.submit(); //一般不要这个.
        //提交程序  并且监控打印程序执行情况
        boolean b = job.waitForCompletion(true);
        System.exit(b ? 0 : 1);
    }


    public static void test6() throws Exception {
        //通过Job来封装本次mr的相关信息

        System.out.println("*******************");

        Configuration conf = new Configuration();
        conf.set("fs.defaultFS", "hdfs://ns1");
        conf.set("dfs.nameservices", "ns1");
        conf.set("dfs.ha.namenodes.ns1", "nn1,nn2");
        conf.set("dfs.namenode.rpc-address.ns1.nn1", "master:9000");
        conf.set("dfs.namenode.rpc-address.ns1.nn2", "slave1:9000");
//            //conf.setBoolean(name, value);
        conf.set("dfs.client.failover.proxy.provider.ns1", "org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider");

        // 即使没有下面这行,也可以本地运行 因\hadoop-mapreduce-client-core-2.7.4.jar!\mapred-default.xml 中默认的参数就是 local
        //conf.set("mapreduce.framework.name","local");
        Job job = Job.getInstance(conf);

        //指定本次mr job jar包运行主类
        job.setJarByClass(T_Hadoop.class);

        //指定本次mr 所用的mapper reducer类分别是什么
        job.setMapperClass(FlowSumMapper.class);
        job.setReducerClass(FlowSumReducer.class);

        //指定本次mr mapper阶段的输出  k  v类型
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(FlowBean.class);

        //指定本次mr 最终输出的 k v类型
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(FlowBean.class);

        // job.setNumReduceTasks(3); //ReduceTask个数

        //如果业务有需求，就可以设置combiner组件
        //job.setCombinerClass(WordCountReducer.class);

        //指定本次mr 输入的数据路径 和最终输出结果存放在什么位置
        //FileInputFormat.setInputPaths(job, "/Users/huangchao/docker/input");
        //FileOutputFormat.setOutputPath(job, new Path("/Users/huangchao/docker/output"));
        //如果出现0644错误或找不到winutils.exe,则需要设置windows环境和相关文件.

        //上面的路径是本地测试时使用，如果要打包jar到hdfs上运行时，需要使用下面的路径。
        FileInputFormat.setInputPaths(job, new Path("hdfs://ns1/wordcount/phonetxt"));
        FileSystem fs = DistributedFileSystem.get(new URI("hdfs://ns1"), conf);
        if (fs.exists(new Path("hdfs://ns1/wordcount/phonetxtout"))) {
            System.out.println("phonetxtout文件已经存在，正在删除...");
            fs.delete(new Path("hdfs://ns1/wordcount/phonetxtout"), true);
        }

        System.out.println("phonetxtout重新生成");
        FileOutputFormat.setOutputPath(job, new Path("hdfs://ns1/wordcount/phonetxtout"));


        /*** 这是使用普通模式访问hadoop***/
//        FileInputFormat.setInputPaths(job, new Path("hdfs://master:9000/wordcount/input"));
//        FileSystem fs = DistributedFileSystem.get(new URI("hdfs://master:9000"), conf);
//        if (fs.exists(new Path("hdfs://master:9000/wordcount/output"))) {
//            System.out.println("output文件已经存在，正在删除...");
//            fs.delete(new Path("hdfs://master:9000/wordcount/output"), true);
//        }
//
//        FileOutputFormat.setOutputPath(job, new Path("hdfs://master:9000/wordcount/output"));
//

        // job.submit(); //一般不要这个.
        //提交程序  并且监控打印程序执行情况
        boolean b = job.waitForCompletion(true);
        System.exit(b ? 0 : 1);
    }

    public static void test7() throws Exception {
        //通过Job来封装本次mr的相关信息

        System.out.println("*******************");

        Configuration conf = new Configuration();
        conf.set("fs.defaultFS", "hdfs://ns1");
        conf.set("dfs.nameservices", "ns1");
        conf.set("dfs.ha.namenodes.ns1", "nn1,nn2");
        conf.set("dfs.namenode.rpc-address.ns1.nn1", "master:9000");
        conf.set("dfs.namenode.rpc-address.ns1.nn2", "slave1:9000");
//            //conf.setBoolean(name, value);
        conf.set("dfs.client.failover.proxy.provider.ns1", "org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider");

        // 即使没有下面这行,也可以本地运行 因\hadoop-mapreduce-client-core-2.7.4.jar!\mapred-default.xml 中默认的参数就是 local
        //conf.set("mapreduce.framework.name","local");
        Job job = Job.getInstance(conf);

        //指定本次mr job jar包运行主类
        job.setJarByClass(T_Hadoop.class);

        //指定本次mr 所用的mapper reducer类分别是什么
        job.setMapperClass(FlowSumSort.FlowSumSortMapper.class);
        job.setReducerClass(FlowSumSort.FlowSumSortReducer.class);

        //指定本次mr mapper阶段的输出  k  v类型
        job.setMapOutputKeyClass(FlowBean.class);
        job.setMapOutputValueClass(Text.class);

        //指定本次mr 最终输出的 k v类型
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(FlowBean.class);

        // job.setNumReduceTasks(3); //ReduceTask个数

        //如果业务有需求，就可以设置combiner组件
        //job.setCombinerClass(WordCountReducer.class);

        //指定本次mr 输入的数据路径 和最终输出结果存放在什么位置
        //FileInputFormat.setInputPaths(job, "/Users/huangchao/docker/input");
        //FileOutputFormat.setOutputPath(job, new Path("/Users/huangchao/docker/output"));
        //如果出现0644错误或找不到winutils.exe,则需要设置windows环境和相关文件.

        //上面的路径是本地测试时使用，如果要打包jar到hdfs上运行时，需要使用下面的路径。
        FileInputFormat.setInputPaths(job, new Path("hdfs://ns1/wordcount/phonetxtout"));
        FileSystem fs = DistributedFileSystem.get(new URI("hdfs://ns1"), conf);
        if (fs.exists(new Path("hdfs://ns1/wordcount/phonetxtoutsort"))) {
            System.out.println("phonetxtout文件已经存在，正在删除...");
            fs.delete(new Path("hdfs://ns1/wordcount/phonetxtoutsort"), true);
        }

        System.out.println("phonetxtout重新生成");
        FileOutputFormat.setOutputPath(job, new Path("hdfs://ns1/wordcount/phonetxtoutsort"));


        /*** 这是使用普通模式访问hadoop***/
//        FileInputFormat.setInputPaths(job, new Path("hdfs://master:9000/wordcount/input"));
//        FileSystem fs = DistributedFileSystem.get(new URI("hdfs://master:9000"), conf);
//        if (fs.exists(new Path("hdfs://master:9000/wordcount/output"))) {
//            System.out.println("output文件已经存在，正在删除...");
//            fs.delete(new Path("hdfs://master:9000/wordcount/output"), true);
//        }
//
//        FileOutputFormat.setOutputPath(job, new Path("hdfs://master:9000/wordcount/output"));
//

        // job.submit(); //一般不要这个.
        //提交程序  并且监控打印程序执行情况
        boolean b = job.waitForCompletion(true);
        System.exit(b ? 0 : 1);
    }
}
