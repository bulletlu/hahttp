package com.cninfo.proxy.http.core;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
//import java.util.Properties;

import javax.servlet.http.HttpServletResponse;

import org.apache.hadoop.io.IOUtils;
import org.apache.log4j.Logger;
import org.apache.tika.Tika;
import org.apache.tika.config.TikaConfig;

public class ResponseResult {
	static Logger logger = Logger.getLogger(ResponseResult.class.getName());
	
	public static final int BUFFER_SIZE = 16*1024;
	
	private static Tika tika;
	
	/**
	 * 
	 */
	public static void initial(){
		TikaConfig config = TikaConfig.getDefaultConfig();
		tika = new Tika(config);
	}

	/**
	 * @param response
	 * @param in
	 * @param path
	 */
	public static void sendResult(HttpServletResponse response, InputStream in,String path) {
		BufferedOutputStream out = null;
		String contentType = tika.detect(path);
		logger.info(path+" ==> "+contentType);
				
		try {
			response.setContentType(contentType);
			out = new BufferedOutputStream(response.getOutputStream());
			byte[] buf = new byte[BUFFER_SIZE];
			int rn = 0;
			while((rn = in.read(buf))> -1){
				out.write(buf, 0, rn);
			}
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(out != null){
				IOUtils.closeStream(out);
				logger.debug("Response outputstream has closed");
			}
			if(in != null) IOUtils.closeStream(in);
		}
	}
		
	/**
	 * @param args
	 */
	public static void main(String[] args){
		ResponseResult.initial();
	}

}
