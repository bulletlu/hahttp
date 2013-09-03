package com.cninfo.proxy.http.core;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.log4j.Logger;

import com.cninfo.proxy.http.ProxyCache;
import com.cninfo.proxy.http.ProxyFileSystem;
import com.cninfo.proxy.http.impl.HdfsFileSystem;
import com.cninfo.proxy.http.impl.StandardCache;

public class ProxyServlet extends HttpServlet {
	private static final long serialVersionUID = 1452199538511044106L;
	static Logger logger = Logger.getLogger(ProxyServlet.class.getName());
	
	private boolean useCache;
	
	
	/**
	 * Constructor of the object.
	 */
	public ProxyServlet() {
		super();
	}

	/**
	 * Destruction of the servlet. <br>
	 */
	public void destroy() {
		super.destroy();
		ProxyFileSystem fs = HdfsFileSystem.getSingleton();
		fs.close();
	}

	/**
	 * The doGet method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to get.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String url = request.getRequestURI().replaceFirst(request.getContextPath(), "");
		//进行路径转换
		logger.info("Source url:"+url);
		Converter converter = Converter.getSingleton();
		url = converter.convert(url);
		logger.info("Convert url:"+url);
		
		ProxyFileSystem fs = HdfsFileSystem.getSingleton();
		ProxyCache cache = StandardCache.getSingleton();
		
		InputStream in = null;
		if(this.useCache && cache.exists(url)){
			in = cache.getInputStream(url);		
		}else if(fs.exists(url)){
			in = fs.getInputStream(url);
		}else{
			logger.error("File '"+url+"' is not found");
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			return;
		}	
		
		ResponseResult.sendResult(response, in, url);
		if(this.useCache && !cache.exists(url)){
			in = fs.getInputStream(url);
			cache.write(in, url);
		}
	}

	/**
	 * The doPost method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to post.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		this.doGet(request, response);
	}

	/**
	 * Initialization of the servlet. <br>
	 *
	 * @throws ServletException if an error occurs
	 */
	
	private FileSystem initial(String hdfsUrl){
		DistributedFileSystem dfs = new DistributedFileSystem();
		org.apache.hadoop.conf.Configuration conf = new org.apache.hadoop.conf.Configuration();       
        try {
            dfs.initialize(new URI(hdfsUrl), conf);
        } catch (Exception e) {
        	logger.error("DFS Initialization error");
        }
        return dfs;
	}
	
	public void init() throws ServletException {
		String useCacheStr = this.getInitParameter("useCache");
		this.useCache = "true".equals(useCacheStr)?true:false;
		
		String cache = this.getInitParameter("cache");
		useCache = useCache && cache != null?true:false;
		
		String hdfsUrl = this.getInitParameter("hdfsUrl");
		
		FileSystem fs = this.initial(hdfsUrl);
		HdfsFileSystem.initial(fs);
		
		StandardCache.initial(cache);
		ResponseResult.initial();
	}

}
