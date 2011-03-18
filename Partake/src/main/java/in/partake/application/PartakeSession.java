package in.partake.application;


import in.partake.model.UserEx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.wicket.protocol.http.WebSession;
import org.apache.wicket.request.Request;
import org.openid4java.consumer.ConsumerException;
import org.openid4java.consumer.ConsumerManager;

public class PartakeSession extends WebSession {
    private static final long serialVersionUID = 1L;
    
    /** 現在ログインしているユーザー */
    private UserEx currentUser;
    
    /** OpenID Manager */
    private static ConsumerManager manager;
    
    /** オブジェクト */
    private Map<String, Object> sessionMap;
    
    /** メッセージ */
    private List<String> infoMessages; 
    private List<String> warningMessages;
    private List<String> errorMessages;
    
    public PartakeSession(Request request) {
        super(request);
        this.currentUser = null;
        this.sessionMap = new HashMap<String, Object>();
        this.infoMessages = new ArrayList<String>();
        this.warningMessages = new ArrayList<String>();
        this.errorMessages = new ArrayList<String>();
        
        try {
            manager = new ConsumerManager();
        } catch (ConsumerException e) {
            e.printStackTrace();
        }        
    }
    
    public static PartakeSession get() {
        return (PartakeSession) WebSession.get();
    }

    // ----------------------------------------------------------------------
    
    public void setCurrentUser(UserEx user) {
        this.currentUser = user;
    }
    
    public UserEx getCurrentUser() {
        return currentUser;
    }
    
    public boolean isLoggedIn() {
        return currentUser != null;
    }
    
    public void removeCurrentUser() {
        this.currentUser = null;
    }
    
    public ConsumerManager getConsumerManager() {
        return manager;
    }
    
    // ----------------------------------------------------------------------
    
    public void put(String key, Object value) {
        sessionMap.put(key, value);
    }

    public Object get(String key) {
        return sessionMap.get(key);
    }
    
    public void remove(String key) {
        sessionMap.remove(key);
    }
    
    // ----------------------------------------------------------------------
    
    public synchronized void addMessage(String message) {
        infoMessages.add(message);
    }
    
    public synchronized void addWarningMessage(String message) {
        warningMessages.add(message);
    }
    
    public synchronized void addErrorMessage(String message) {
        errorMessages.add(message);
    }
    
    public synchronized List<String> getMessages() {
        return infoMessages;
    }
    
    public synchronized List<String> getWarningMessages() {
        return warningMessages;
    }
    
    public synchronized List<String> getErrorMessages() {
        return errorMessages;
    }
    
    public synchronized void removeMessages() {
        infoMessages = new ArrayList<String>();
        warningMessages = new ArrayList<String>();
        errorMessages = new ArrayList<String>();
    }
}
