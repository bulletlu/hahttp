package com.cninfo.proxy.http.core;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * @author lunianping
 *
 */
public class Converter {
	static Logger logger = Logger.getLogger(Converter.class.getName());
	
	public static final String MAPPING_RULE = "mapping.properties";
	private Properties rule;
	private static Converter singleton;
	
	/**
	 * 
	 */
	private Converter(){
		rule = new Properties();
		InputStream in = Converter.class.getClassLoader().getResourceAsStream(MAPPING_RULE);
		try {
			rule.load(in);
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("加载"+MAPPING_RULE+"失败");
		}
	}
	
	public static Converter getSingleton(){
		if(singleton == null){
			synchronized(Converter.class){
				singleton = new Converter();
			}
		}
		return singleton;
	}
	
	public void addRule(String key,String value){
		this.rule.put(key, value);
	}
	
	public String convert(String src) {
		String val = src;
		for(Object key: rule.keySet()){
			val = val.replaceAll((String)key, (String)rule.get(key));			
		}
		return val;
	}

}
