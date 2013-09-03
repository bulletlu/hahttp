package com.cninfo.proxy.http;

import java.io.IOException;
import java.io.InputStream;

public interface ProxyFileSystem {
	/**
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public InputStream getInputStream(String path) throws IOException ;
	/**
	 * @param path
	 * @return
	 */
	public boolean exists(String path);
	/**
	 * 
	 */
	public void close();
}
