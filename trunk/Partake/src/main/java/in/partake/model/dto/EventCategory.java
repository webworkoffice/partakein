package in.partake.model.dto;

import in.partake.util.KeyValuePair;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class EventCategory {
    public static final List<KeyValuePair> CATEGORIES = Collections.unmodifiableList(Arrays.asList(
                    new KeyValuePair("meeting", "懇親会・オフ会"),
                    new KeyValuePair("computer", "コンピューター"),
                    new KeyValuePair("sports", "スポーツ"),
                    new KeyValuePair("game", "ゲーム"),
                    new KeyValuePair("watching", "鑑賞(芸術、映画)"),
                    new KeyValuePair("neta", "ネタ"),
                    new KeyValuePair("others", "その他")
            ));
            
    public static boolean isValidCategoryName(String categoryName) {
        for (KeyValuePair kv : CATEGORIES) {
            if (kv.getKey().equals(categoryName)) { return true; }
        }
        
        return false;
    }
    
    public static String getReadableCategoryName(String categoryName) {
        for (KeyValuePair kv : CATEGORIES) {
            if (kv.getKey().equals(categoryName)) { return kv.getValue(); }
        }
        
        return "-"; // TODO: should throw some Exception?
    }
}
