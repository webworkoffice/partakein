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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import com.twitter.Regex;


public final class Util {
	private static final Logger logger = Logger.getLogger(Util.class);
	
	private static final Pattern REMOVETAG_PATTERN = Pattern.compile("(<!--.+?-->)|(<.+?>)", Pattern.DOTALL | Pattern.MULTILINE);
	
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
        return Regex.AUTO_LINK_HASHTAGS.matcher(hashTag).matches();
    }
    
    public static int codePointCount(String s) {
    	return s.codePointCount(0, s.length());
    }
    
    public static String substring(String source, int startCodePoints) {
        final int endCodePoints = source.codePointCount(0, source.length());
        return substring(source, startCodePoints, endCodePoints);
    }

    public static String substring(String source, int startCodePoints, int endCodePoints) {
        final int startIndex = source.offsetByCodePoints(0, startCodePoints);
        final int endIndex = source.offsetByCodePoints(startIndex, endCodePoints - startCodePoints);
        return source.substring(startIndex, endIndex);
    }
    
    public static String shorten(String message, int maxLength) {
        if (message.codePointCount(0, message.length()) <= maxLength) { return message; }
        
        return substring(substring(message, 0, Math.max(maxLength - 3, 0)) + "...", 0, maxLength);
    }
    
    // ----------------------------------------------------------------------
	// Image
    
    public static boolean isImageContentType(String s) {
    	if (s == null) { return false; }
    	
    	if ("image/jpeg".equals(s)) { return true; }
    	if ("image/png".equals(s)) { return true; }
    	if ("image/gif".equals(s)) { return true; }
    	if ("image/pjpeg".equals(s)){ return true; }
    	
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
        return REMOVETAG_PATTERN.matcher(html).replaceAll("");
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
    
    public static String shortenURL(String sourceURL) {
        return bitlyShortURL(sourceURL);
    }
    
    public static String bitlyShortURL(Bitly bitly, String sourceURL) {
        try {
            BitlyUrl bUrl = bitly.shorten(sourceURL);
            return bUrl.getShortUrl().toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * URL を bitly で短縮する。
     * 名前が良くないので、deprecated 扱い。これからは shortenURL を使うこと。
     * TODO: あと、これがここにいるのはよくないんじゃないかなー。URLService 的なものを作った方がよいような気がする。
     * 
     * @param sourceURL
     * @return
     */
    @Deprecated
    public static String bitlyShortURL(String sourceURL) {
        final String bitlyUserName = PartakeProperties.get().getBitlyUserName();
        final String bitlyAPIKey = PartakeProperties.get().getBitlyAPIKey();
        final Bitly bitly = BitlyFactory.newInstance(bitlyUserName, bitlyAPIKey);
        
        return bitlyShortURL(bitly, sourceURL);
    }
}
