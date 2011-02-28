package in.partake.view;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import in.partake.resource.Constants;
import in.partake.util.Util;

import org.owasp.validator.html.AntiSamy;
import org.owasp.validator.html.CleanResults;
import org.owasp.validator.html.Policy;
import org.owasp.validator.html.PolicyException;
import org.owasp.validator.html.ScanException;

/**
 * View に関する定型処理をまとめたもの。
 * TODO: 一部の関数はまだ Helper や Util にいるかもしれませんが、これは ViewHelper に移動予定です。
 * 
 * @author shinyak
 *
 */
public final class ViewHelper {
    private static Policy antiSamyPolicy;
    
    static {
        try {
            InputStream is = new BufferedInputStream(Util.createInputSteram(Constants.ANTISAMY_POLICY_FILE_RELATIVE_LOCATION));
            antiSamyPolicy = Policy.getInstance(is);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (PolicyException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * escapeHTML の短縮関数名
     * @param s
     * @return
     */
    public static String h(String s) {
        return escapeHTML(s);
    }
    
    /**
     * HTML で利用されるタグなどを escape する。
     * @param s
     * @return
     */
    public static String escapeHTML(String s) {
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
                if (Character.isIdentifierIgnorable(s.codePointAt(i))) {
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

    
    public static String cleanupText(String dirtyText) {
        String s = dirtyText;
        
        if (s == null) { return ""; }
        
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < s.length(); i += Character.charCount(s.codePointAt(i))) {
            switch (s.codePointAt(i)) {
            case '&': builder.append("&amp;"); break;
            case '<': builder.append("&lt;"); break;
            case '>': builder.append("&gt;"); break;
            case '"': builder.append("&quot;"); break;
            case '\'': builder.append("&apos;"); break;
            case '\n': builder.append("<br />"); break;
            default:                
                if (Character.isIdentifierIgnorable(s.codePointAt(i))) {
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

    
    /**
     * HTML の script などを取り除く
     * @param dirtyHTML
     * @return
     */
    public static String cleanupHTML(String dirtyHTML) {
        // Hmm...
        if (antiSamyPolicy == null) { return ""; }
        
        try {
            AntiSamy antiSamy = new AntiSamy();
            CleanResults cr = antiSamy.scan(dirtyHTML, antiSamyPolicy);
            
            return cr.getCleanHTML();
        } catch (PolicyException e) {
            e.printStackTrace();
            return "";
        } catch (ScanException e) {
            e.printStackTrace();
            return "";
        }
    }
    
}
