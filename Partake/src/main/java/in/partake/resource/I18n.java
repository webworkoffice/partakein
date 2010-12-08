package in.partake.resource;

import java.util.ResourceBundle;

public class I18n {
    private static I18n instance = new I18n();
    
    private ResourceBundle bundle;
    
    public static I18n get() {
        return instance;
    }

    public static String t(String key) {
        return get().getBundle().getString(key);
    }
    

    private I18n() {
        // TODO: これ native2ascii とかしないとだめなんだよねえ。泣ける。どうしよう。
        bundle = ResourceBundle.getBundle("i18n.resource");
    } 
    
    public ResourceBundle getBundle() {
        return bundle;
    }
}
