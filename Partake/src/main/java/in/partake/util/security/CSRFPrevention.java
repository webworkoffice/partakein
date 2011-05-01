package in.partake.util.security;


import in.partake.util.Util;

import java.util.LinkedHashSet;

/**
 * CSRF 対策と二重投稿防止。
 *  CSRF 対策には、sessionToken を付与し、token check を行う。
 *  二重投稿の防止には、onetime token を付与し、token が複数回使われていると invalid token 扱いとする。
 *    onetime token は使われているかどうかしかチェックしない。
 * @author shinyak
 *
 */
public final class CSRFPrevention {
    private String sessionToken;
    private LinkedHashSet<String> consumedOnetimeTokens;
    
    public CSRFPrevention() {
        sessionToken = Util.randomString(32);
        consumedOnetimeTokens = new LinkedHashSet<String>();
    }
    
    public String getSessionToken() {
        return sessionToken;
    }
    
    public boolean isValidSessionToken(String token) {
        return sessionToken.equals(token);
    }
    
    public String issueOnetimeToken() {
        return Util.randomString(32);
    }
    
    public synchronized boolean isConsumed(String token) {
        return consumedOnetimeTokens.contains(token);
    }
    
    /** token を consume する。 */
    public synchronized void consumeToken(String token) {
        consumedOnetimeTokens.add(token);
        
        // 10 個を超えて consumed token が現れれば、メモリ節約のために除去する。
        while (consumedOnetimeTokens.size() > 10) {
            consumedOnetimeTokens.remove(consumedOnetimeTokens.iterator().next());
        }
    }
}
