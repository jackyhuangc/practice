package com.jacky.practice;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;

public class T_Hadoop {
    public static void main(String[] args) {
        try {
            Configuration conf = new Configuration();

            // 不设置该代码会出现错误：java.io.IOException: No FileSystem for scheme: hdfs
            conf.set("fs.hdfs.impl", "org.apache.hadoop.hdfs.DistributedFileSystem");

            String filePath = "hdfs://master:9000/jacky/hello1.txt";
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
}
