package com.jacky.practice;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Description Here!
 * 
 * @author Jacky Huang
 * @date 2018-02-04 12:28
 * @since jdk1.8
 */
public class NetworkClassLoader extends ClassLoader {

	private String rootUrl;

	public NetworkClassLoader(String rootUrl) {
		this.rootUrl = rootUrl;
	}

	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		Class clazz = null;
		byte[] classData = this.getClassData(name);
		if (classData == null)
			throw new ClassNotFoundException();
		clazz = this.defineClass(name, classData, 0, classData.length);
		return clazz;
	}

	private byte[] getClassData(String name) {
		InputStream is = null;
		try {
			String path = classNameToPath(name);
			URL url = new URL(path);
			byte[] buff = new byte[1024 * 4];
			int len = -1;
			is = url.openStream();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			while ((len = is.read(buff)) != -1) {
				baos.write(buff, 0, len);
			}
			return baos.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	private String classNameToPath(String name) {
		return rootUrl + "/" + name.replace(".", "/") + ".class";
	}
}
