package com.jacky.practice;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;

/**
 * Description Here!
 *
 * @author Jacky Huang
 * @date 2018-02-02 11:04
 * @since jdk1.8
 */
public class T_FileStreamTest {

	// throws语句用来表明方法不能处理的异常( throws总是出现在一个函数头中，用来标明该成员函数可能抛出的各种异常)。
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		// FileOutputStream是字节流，如果是写入汉字，会又乱码，因为一个汉字是2个字节，无法一次写入
		FileOutputStream out = new FileOutputStream("file.dat");

		// byte[] b = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
		// out.write(b);
		// out.close();

		// 解决
		OutputStreamWriter oStreamWriter = new OutputStreamWriter(out, "utf-8");
		oStreamWriter.write("因为一个汉字是2个字节，无法一次写入");
		oStreamWriter.close();

		FileInputStream in = new FileInputStream("file.dat");
		// in.skip(9); // 跳过前面的9个字节
		// int c = in.read();
		// System.out.println(c); // 输出为10
		// in.close();

		RandomAccessFile inR = new RandomAccessFile("file.dat", "r");
		inR.skipBytes(9);
		int c = inR.readByte();
		System.out.println(c); // 输出为10
		inR.close();

		InputStreamReader oStreamReader = new InputStreamReader(in, "utf-8");
		StringBuffer sb = new StringBuffer();
		while (oStreamReader.ready()) {
			sb.append((char) oStreamReader.read());
		}

		System.out.print(sb.toString());
		oStreamReader.close();
		in.close();

	}

}
