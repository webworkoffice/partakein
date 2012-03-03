package in.partake.base;

import in.partake.resource.PartakeProperties;
import in.partake.view.util.Helper;

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
import java.util.Random;
import java.util.UUID;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.rosaloves.bitlyj.Bitly;
import com.rosaloves.bitlyj.Bitly.Provider;
import com.rosaloves.bitlyj.BitlyException;
import com.rosaloves.bitlyj.ShortenedUrl;
import com.twitter.Regex;


public final class Util {
	private static final Logger logger = Logger.getLogger(Util.class);
	private static final Random random = new Random();

	private static final Pattern REMOVETAG_PATTERN = Pattern.compile("(<!--.+?-->)|(<.+?>)", Pattern.DOTALL | Pattern.MULTILINE);
	private static final String ALNUM = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    // ----------------------------------------------------------------------
    // UUID
	
	public static boolean isUUID(String str) {
	    try {
	        UUID.fromString(str);
	        return true;
	    } catch (IllegalArgumentException e) {
	        return false;
	    }
	}

	// ----------------------------------------------------------------------
	// Time

	public static Date dateFromTimeString(String timeString) {
		try {
			return new Date(Long.parseLong(timeString));
		} catch (NumberFormatException e) {
		    logger.error("dateFromTimeString failed.", e);
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

    // Use StringUtils.isEmpty() instead.
    @Deprecated
    public static boolean isEmpty(String str) {
        return StringUtils.isEmpty(str);
    }

    public static boolean isValidHashtag(String hashTag) {
        return Regex.AUTO_LINK_HASHTAGS.matcher(hashTag).matches();
    }

    public static String randomString(int length) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; ++i) {
            builder.append(ALNUM.charAt(random.nextInt(ALNUM.length())));
        }
        return builder.toString();
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

        return substring(substring(message, 0, Math.max(maxLength - 3, 0)) + "...", 0, Math.max(maxLength, 0));
    }

    /**
     * 文字列から'#'と後続の文字列を取り除いたものを返す。
     * URLから # + fragment を取り除区などの用途を想定。
     *
     * @param str 改行を含まない加工対象文字列
     * @return '#'と後続の文字列を取り除いた文字列
     */
    public static String removeURLFragment(String str) {
        if (str == null) { return null; }
        return str.replaceAll("#.*", "");
    }

    @Deprecated
    public static String removeHash(String str) {
        return removeURLFragment(str);
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
    	if (file == null)
    	    return new byte[0];

    	InputStream is = new BufferedInputStream(new FileInputStream(file));
    	return getContentOfInputStream(is);
    }
    
    public static byte[] getContentOfInputStream(InputStream is) throws IOException {
        if (is == null)
            return null;
        
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

    public static InputStream createInputSteram(String resource) throws IOException {
        InputStream stream = Util.class.getResourceAsStream(resource);
        if (stream != null) { return stream; }

        stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(resource);
        if (stream != null) { return stream; }

        try {
            return new FileInputStream(new File(resource));
        } catch (Exception e) {
            return null;
        }
    }


    // ----------------------------------------------------------------------
	// HTML

    // TODO: These functions are should be moved to Helper.
    // HTML escape
    @Deprecated
    public static String h(String s) {
        return Helper.escapeHTML(s);
    }

    @Deprecated
    public static String cleanupText(String dirtyText) {
        return Helper.cleanupText(dirtyText);
    }

    @Deprecated
    public static String cleanupHTML(String dirtyHTML) {
        return Helper.cleanupHTML(dirtyHTML);
    }

    /**
     * validなHTMLから、HTMLタグとコメントを取り除く。
     *
     * @param html 加工するHTML文字列
     * @return HTMLタグとコメントを取り除いた文字列
     */
    public static String removeTags(String html) {
        if (html == null) { return null; }
        return REMOVETAG_PATTERN.matcher(html).replaceAll("");
    }

    // ----------------------------------------------------------------------
	// URI

    // escapeURI の代わりに encodeURI を使うこと。encodeURIComponent
    @Deprecated
    public static String escapeURI(String s) {
        return encodeURI(s);
    }

    // URLへの文字列埋込みではencodeURIComponentを使うべき＆他の使い道がない
    @Deprecated
    public static String encodeURI(String s) {
        if (s == null) { return ""; }
        try {
            return URLEncoder.encode(s, "utf-8");
        } catch (UnsupportedEncodingException e) {
            logger.warn("safely returns empty string.", e);
            return "";
        }
    }

    /**
     * Javascriptの同名関数と同様、
     * 文字列をURIのパラメータとして使用できるようにエンコードを施す。
     * @see https://developer.mozilla.org/en/JavaScript/Reference/Global_Objects/encodeURIComponent
     */
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
            logger.warn("safely returns empty string.", e);
            return "";
        }
    }

    /**
     * URL を bitly で短縮する。
     * TODO: これがここにいるのはよくないんじゃないかなー。URLService 的なものを作った方がよいような気がする。
     *
     * @param sourceURL
     * @return
     */
    public static String callBitlyShortenURL(String sourceURL) throws BitlyException {
    	if (sourceURL.startsWith("http://localhost") || sourceURL.startsWith("http://127.0.0.1")) {  
    		// bitly API may throw Exception if its argument means localhost
    		logger.debug(String.format("avoid shortening URL(%s)", sourceURL));
    		return sourceURL;
    	}
        final String bitlyUserName = PartakeProperties.get().getBitlyUserName();
        final String bitlyAPIKey = PartakeProperties.get().getBitlyAPIKey();
        final Provider bitly = Bitly.as(bitlyUserName, bitlyAPIKey);

        ShortenedUrl bUrl = bitly.call(Bitly.shorten(sourceURL));
        return bUrl.getShortUrl().toString();
    }
}
