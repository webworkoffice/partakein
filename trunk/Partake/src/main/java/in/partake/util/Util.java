package in.partake.util;

import in.partake.resource.Constants;
import in.partake.resource.PartakeProperties;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import java.net.URLEncoder;
import java.util.Date;
import java.util.Formatter;

import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.owasp.validator.html.AntiSamy;
import org.owasp.validator.html.CleanResults;
import org.owasp.validator.html.Policy;
import org.owasp.validator.html.PolicyException;
import org.owasp.validator.html.ScanException;

import com.rosaloves.net.shorturl.bitly.Bitly;
import com.rosaloves.net.shorturl.bitly.BitlyFactory;
import com.rosaloves.net.shorturl.bitly.url.BitlyUrl;


public final class Util {
	private static final Logger logger = Logger.getLogger(Util.class);
	
	// ----------------------------------------------------------------------
	// Time 
	
	public static Date dateFromTimeString(String timeString) {
		try {
			return new Date(Long.parseLong(timeString));
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return null;
		}
	}
	
    public static String getTimeString(Date date) {
        return getTimeString(date.getTime());
    }

    public static String getTimeString(long time) {
        return new Formatter().format("%020d", time).toString();
    }
    
    public static String getReversedTimeString(Date date) {
        return getReversedTimeString(date.getTime());
    }
    
    public static String getReversedTimeString(long time) {
        return new Formatter().format("%020d", Long.MAX_VALUE - time).toString();
    }
    
    public static Date oneDayBefore(Date date) {
        return new Date(date.getTime() - 1000 * 3600 * 24); 
    }

    public static Date halfDayBefore(Date date) {
        return new Date(date.getTime() - 1000 * 3600 * 12); 
    }

	// ----------------------------------------------------------------------
	// Text
    
    public static boolean isEmpty(String str) {
    	if (str == null) { return true; }
    	if ("".equals(str)) { return true; }
    	return false;
    }
    
    public static boolean isValidHashtag(String hashTag) {
    	return hashTag.matches("#[a-zA-z0-9_\\-]+");
    }
    
    // ----------------------------------------------------------------------
	// Image
    
    public static boolean isImageContentType(String s) {
    	if (s == null) { return false; }
    	
    	if ("image/jpeg".equals(s)) { return true; }
    	if ("image/png".equals(s)) { return true; }
    	if ("image/gif".equals(s)) { return true; }
    	if ("image/pjpeg".equals(s)){ return true; }
    	
    	System.out.println("IMAGE CONTENT TYPE: " + s);
    	
    	return false;
    }
    
    /**
     * file の内容を byte array に変換する
     */
    public static byte[] getContentOfFile(File file) throws IOException {
    	if (file == null) { return new byte[0]; }
    	
    	InputStream is = new BufferedInputStream(new FileInputStream(file));
    	try {
	    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    	final int SIZE = 1024 * 1024;
	    	byte[] buf = new byte[SIZE];
	    	
	    	int len;
	    	while ((len = is.read(buf)) > 0) {
	    		baos.write(buf, 0, len);
	    	}
	    	
	    	return baos.toByteArray();
    	} finally {
    		is.close();
    	}
    }
    
    
    // ----------------------------------------------------------------------
	// HTML  
    
    @Deprecated
    public static String h(Object o) {
        if (o == null) { return ""; }
        return h(o.toString());
    }
    
    // HTML escape
    public static String h(String s) {
        if (s == null) { return ""; }
        
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < s.length(); i += Character.charCount(s.codePointAt(i))) {
        	switch (s.codePointAt(i)) {
            case '&': builder.append("&amp;"); break;
            case '<': builder.append("&lt;"); break;
            case '>': builder.append("&gt;"); break;
            case '"': builder.append("&quot;"); break;
            case '\'': builder.append("&apos;"); break;
            	default:  
            	if (Character.isISOControl(s.codePointAt(i))) {
            		// ignore.
            	} else {
            		for (int j = 0; j < Character.charCount(s.codePointAt(i)); ++j) {
            			builder.append(s.charAt(i + j));
            		}
            	}
        	}
        }
        
        return builder.toString();
    }
    
    public static String encodeURIComponent(String uri) {
    	try {
    		return URLEncoder.encode(uri, "UTF-8")
    	                         .replaceAll("\\+", "%20")
    	                         .replaceAll("\\%21", "!")
    	                         .replaceAll("\\%27", "'")
    	                         .replaceAll("\\%28", "(")
    	                         .replaceAll("\\%29", ")")
    	                         .replaceAll("\\%7E", "~");
    	} catch (UnsupportedEncodingException e) {
    		logger.warn("Util#encodeURIComponent() safely returns empty string.");
    		return "";
    	}
    }
    
    public static String cleanupHTML(String dirtyHTML) {
    	try {
    		String fileName = ServletActionContext.getServletContext().getRealPath(Constants.ANTISAMY_POLICY_FILE_RELATIVE_LOCATION);     			
	    	Policy policy = Policy.getInstance(fileName);
	
	    	AntiSamy as = new AntiSamy();
	    	CleanResults cr = as.scan(dirtyHTML, policy);
	    	
	    	return cr.getCleanHTML();
    	} catch (PolicyException e) {
    		e.printStackTrace();
    		return "";
    	} catch (ScanException e) {
			e.printStackTrace();
			return "";
		}
    }
    
    public static String removeTags(String html) {
    	// .+? の ? は最短マッチを表す。
    	return html.replaceAll("<.+?>", "");
    }
    
    // ----------------------------------------------------------------------
	// URI
    
    public static String escapeURI(String s) {
    	if (s == null) { return ""; }
    	try {
			return URLEncoder.encode(s, "utf-8");
		} catch (UnsupportedEncodingException e) {			
			e.printStackTrace();
			return "(encoding-error)";
		}
    }
    
    public static String bitlyShortURL(Bitly bitly, String sourceURL) {
        try {
        	// System.out.println("bitlyShortURL = " + sourceURL);
            BitlyUrl bUrl = bitly.shorten(sourceURL);
            return bUrl.getShortUrl().toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static String bitlyShortURL(String sourceURL) {
        final String bitlyUserName = PartakeProperties.get().getBitlyUserName();
        final String bitlyAPIKey = PartakeProperties.get().getBitlyAPIKey();
        final Bitly bitly = BitlyFactory.newInstance(bitlyUserName, bitlyAPIKey);
        
        return bitlyShortURL(bitly, sourceURL);
    }
}
