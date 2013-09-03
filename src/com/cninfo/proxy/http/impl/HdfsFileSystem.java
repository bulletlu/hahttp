package com.cninfo.proxy.http.impl;

import java.io.IOException;
import java.io.InputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.log4j.Logger;

import com.cninfo.proxy.http.ProxyFileSystem;

public class HdfsFileSystem implements ProxyFileSystem {
	static Logger logger = Logger.getLogger(HdfsFileSystem.class.getName());
	private FileSystem fs;
	private static ProxyFileSystem singleton;
	
	private HdfsFileSystem(FileSystem fs){
		this.fs = fs;
	}
			
	public static ProxyFileSystem getSingleton(){
		if(singleton == null){
			throw new IllegalStateException("缓存尚未初始化，请先调用initial方法");
		}
		return singleton;		
	}
	
	public static void initial(FileSystem fs){
		if(singleton == null){
			synchronized(HdfsFileSystem.class){
				singleton = new HdfsFileSystem(fs);
			}
		}
	}

	public InputStream getInputStream(String path) throws IOException {
		InputStream in =  fs.open(new Path(path));
		return in;
	}
	
	public boolean exists(String path){
		boolean re = false;
		try {
			re = fs.exists(new Path(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return re;
	}
	
	public void close(){
		try {
			fs.close();
			logger.debug("hdfs have closed");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
