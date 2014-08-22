package org.p365.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ReadConfig {
	
	public static String ReadConfigs(String key) {
		InputStream inputStream = ReadConfig.class.getClassLoader()
				.getResourceAsStream("ipConfig.properties");
		
		Properties p = new Properties();
		try {
			p.load(inputStream);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return p.getProperty(key);
	}

}
