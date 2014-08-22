package org.p365.util;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import org.p365.Constant;

/**
 * This class is responsible for loading in configuration data for the
 * application. By default, it searches for a properties located at
 * /usr/local/work/work.properties,  These values are accessed
 * as a standard map by calling Settings.get("Worker", "queues").
 * Implemented as a singleton to avoid reading the file in multiple times.
 * Changes to application configuration require a restart to take effect.
 * @author zqs1886
 *
 */

public class Setting {

	public static Properties configuration =  new Properties();
	
	// Load the settings once on initialization, and hang onto them.
    private static final Setting INSTANCE = new Setting();
    
    @SuppressWarnings("unchecked")
    private Setting() {
        String settingsFile = System.getProperty("configFile");
        //if (settingsFile == null) settingsFile = "/usr/local/p365work/worker.properties";
        if (settingsFile == null) settingsFile = "f:/java/worker.properties";
        try {
        	InputStream inStream = new FileInputStream(settingsFile);
        	configuration.load(inStream);
        } catch (Exception e) {
            // Logging to Stdout here because Log4J not yet initialized.
        	e.printStackTrace();
        }

    }

    public static Setting get() {
        return INSTANCE;
    }


    /**
     * Fetches a setting from configuration.
     *
     * @param key      Actual setting to retrieve
     * @return value of setting as a String or null
     */
    public static String get(String key) {
        String result = null;

        try {
            result = configuration.getProperty(key);
        } catch (NullPointerException e) {
            System.out.println("Warning - unable to load " + 
                key + " from configuration file.");
        }

        return result;
    }

    /**
     * Fetches a setting from  configuration.
     *
     * @param key      Actual setting to retrieve
     * @param defaultValue value to return if setting doesn't exist
     * @return value of setting as a String or null
     */
    public static String get(String key, String defaultValue) {
        return configuration.getProperty(key, defaultValue);
    }


    // Fetches a value from settings as an integer, with a default value.
    public static Integer getIntFromConfig(String key, Integer defaultValue) {
        int result = defaultValue;

		try {
			result = Integer.parseInt(get(key));
		} catch (NumberFormatException e) {
			System.out.println("Error reading settings.");
		}
		
		if(0 == result)
			result = defaultValue;

        return result;
    }

    // Fetches a setting from  config and converts it to a boolean.
    // No boolean settings are autodetected, so that logic is not needed here.
    public static boolean getAsBoolean(String key) {
        return Boolean.valueOf(get(key)).booleanValue();
    }
    
    
	public static void main(String[] args) {
		// TODO Auto-generated method stub
        System.out.println(Setting.getIntFromConfig("thumbnail1_width", Constant.THUMBNAIL1_WIDTH));
	}

}
