package in.partake.resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PartakeProperties {
	private static final PartakeProperties instance = new PartakeProperties();
	private String mode;
	private Properties properties;
	
	public static PartakeProperties get() {
		return instance;
	}

	private PartakeProperties() {
	    this.mode = fetchMode();
		this.properties = readFrom("/partake." + mode + ".properties");
	}
	
	public String getMode() {
	    return this.mode;
	}

	public String getTwitterAdminName() {
		return properties.getProperty("in.partake.twitter.admin");
	}
	
	public String getLuceneIndexDirectory() {
	    return properties.getProperty("in.partake.lucene.indexdir");
	}
	
	public String getBitlyUserName() {
	    return properties.getProperty("in.partake.bitly.username");
	}
	
	public String getBitlyAPIKey() {
	    return properties.getProperty("in.partake.bitly.apikey");
	}

	public String getCassandraHost() {
	    return properties.getProperty("in.partake.cassandra.host");
	}
	
	public int getCassandraPort() {
	    return Integer.parseInt(properties.getProperty("in.partake.cassandra.port"));
	}
	
	public String getTopPath() {
		return properties.getProperty("in.partake.toppath");
	}
	
	// --------------------------------------------------
	
	/** read partake.properties and load.*/
	private String fetchMode() {
        Properties properties = readFrom("/partake.properties");
        if (properties == null) {
            throw new RuntimeException("partake.properties does not exist.");
        }

        return properties.getProperty("in.partake.mode");
    }
	
	private Properties readFrom(String resourceName) {
        Properties properties = new Properties(); 
        InputStream inputStream = null;
        try {
            inputStream = getClass().getResourceAsStream(resourceName);
            properties.load(inputStream);
            
            return properties;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        
        return null;
	}

}
