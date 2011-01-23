package in.partake.resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/** Partake の動作に必要なデータを、partake.properties, 及び {mode}.partake.properties から読み出す。*/
public class PartakeProperties {
	private static final PartakeProperties instance = new PartakeProperties();
	private String mode;
	private Properties properties;
	
	public static PartakeProperties get() {
		return instance;
	}

	private PartakeProperties() {
	    reset();
	}
	
	/** mode 名を用いて読みなおす。初期化及びユニットテスト用途。 */
	public void reset(String mode) {
	    this.mode = mode;
        this.properties = readFrom("/" + mode + ".partake.properties");
	}
	
	/** mode 名を fetch してから読みなおす。初期化及びユニットテスト用途。 */
	public void reset() {
	    reset(fetchMode());
	}
	
	// ----------------------------------------------------------------------
	
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
	
	public String getDAOFactoryClassName() {
	    String str = properties.getProperty("in.partake.database.daofactory");
	    if (str == null) {
	        return "in.partake.model.dao.cassandra.CassandraDAOFactory";
	    } else {
	        return str;
	    }
	}
	
	public String getConnectionPoolClassName() {
	    String str = properties.getProperty("in.partake.database.pool");
        if (str == null) {
            return "in.partake.model.dao.cassandra.CassandraConnectionPool";
        } else {
            return str;
        }
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
	
	public String getGoogleAnalyticsCode() {
		return properties.getProperty("in.partake.analytics.google");
	}
	
	public boolean isEnabledTwitterDaemon() {
	    String str = properties.getProperty("in.partake.twitterdaemon.disabled");
	    if (str == null) { return true; }
	    if ("true".equals(str)) { return false; }
	    return true;
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
