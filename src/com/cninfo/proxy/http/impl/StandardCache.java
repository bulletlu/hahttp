package com.cninfo.proxy.http.impl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.hadoop.io.IOUtils;
import org.apache.log4j.Logger;

import com.cninfo.proxy.http.ProxyCache;

public class StandardCache implements ProxyCache {
	static Logger logger = Logger.getLogger(StandardCache.class.getName());
	
	private static ProxyCache singleton;
	private File cache;

	private StandardCache(){		
	}
	
	/**
	 * @param path
	 */
	private StandardCache(String path){	
		this.cache = new File(path);
		if(cache.exists()){
			if(!cache.isDirectory() || !cache.canWrite()){
				throw new IllegalStateException("缓存不可写或其它原因");
			}
		}else if(!cache.mkdirs()){
			throw new IllegalStateException("创建缓存目录失败");
		}
	}
			
	/**
	 * @return
	 */
	public static ProxyCache getSingleton(){
		if(singleton == null){
			throw new IllegalStateException("缓存尚未初始化，请先调用initial方法");
		}
		return singleton;		
	}
	
	/**
	 * @param root
	 */
	public static void initial(String root){
		if(singleton == null){
			synchronized(StandardCache.class){
				singleton = new StandardCache(root);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see com.cninfo.proxy.http.ProxyCache#getInputStream(java.lang.String)
	 */
	public InputStream getInputStream(String path) throws IOException{
		File file = new File(cache.getPath()+path);
		InputStream in = new FileInputStream(file);
		return in;
	}

	/* (non-Javadoc)
	 * @see com.cninfo.proxy.http.ProxyCache#exists(java.lang.String)
	 */
	public boolean exists(String path){
		File file = new File(cache.getPath()+path);
		return file.exists() && file.canRead();
	}
	
	/* (non-Javadoc)
	 * @see com.cninfo.proxy.http.ProxyCache#writeable(java.lang.String)
	 */
	public boolean writeable(String path){
		File file = new File(cache.getPath()+path);
		return file.canWrite();
	}

	/* (non-Javadoc)
	 * @see com.cninfo.proxy.http.ProxyCache#write(java.io.InputStream, java.lang.String)
	 */
	public void write(InputStream in, String path) throws IOException{
		File file = new File(cache.getPath()+path);
		File parent = file.getParentFile();
		if(!parent.exists()&& !parent.mkdirs()){
			throw new IllegalStateException(parent.getPath()+"缓存文件目录创建失败");
		}
		
		BufferedOutputStream out = null;
		BufferedInputStream bin = null;
		try {
			out = new BufferedOutputStream(new FileOutputStream(file));
			bin = new BufferedInputStream(in);
			byte[] buf = new byte[1024];
			int len = 0;
			while((len = bin.read(buf))>0){
				out.write(buf, 0, len);
			};
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
			throw new IOException(path+"文件写入失败");
		}finally{
			if(out != null){
				IOUtils.closeStream(out);
				logger.debug("FileOutputStream has closed");
			}
		}
	}
}
