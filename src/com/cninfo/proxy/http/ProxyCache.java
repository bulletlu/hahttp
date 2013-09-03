package com.cninfo.proxy.http;

import java.io.IOException;
import java.io.InputStream;

public interface ProxyCache {
	/*
	 * 判断文件是否在缓存中
	 */
	public boolean exists(String path);
	/**
	 * @param path
	 * @return
	 */
	public boolean writeable(String path);
	
	/*
	 * 获取文件
	 */
	public InputStream getInputStream(String path) throws IOException ;
	
	/*
	 * 将文件写入缓存中
	 */
	public void write(InputStream out,String path) throws IOException;
}
