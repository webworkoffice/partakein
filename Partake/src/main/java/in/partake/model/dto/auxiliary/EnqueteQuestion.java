package in.partake.model.dto.auxiliary;

import in.partake.base.IJSONable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class EnqueteQuestion implements IJSONable {
    private String text;
    private EnqueteAnswerType type;
    private List<String> options;

    public EnqueteQuestion(String text, EnqueteAnswerType type, List<String> options) {
        this.text = text;
        this.type = type;
        if (options != null)
            this.options = new ArrayList<String>(options);
    }

    public EnqueteQuestion(JSONObject obj) {
        this.text = obj.getString("text");
        this.type = EnqueteAnswerType.safeValueOf(obj.getString("type"));
        this.options = new ArrayList<String>();

        JSONArray array = obj.getJSONArray("options");
        for (int i = 0; i < array.size(); ++i)
            options.add(array.getString(i));
    }

    @Override
    public JSONObject toJSON() {
        JSONObject obj = new JSONObject();
        obj.put("text", text);
        obj.put("type", type.toString());

        JSONArray array = new JSONArray();
        for (String str : options)
            array.add(str);
        obj.put("options", array);

        return obj;
    }

    public String getText() {
        return text;
    }

    public EnqueteAnswerType getAnswerType() {
        return type;
    }

    public List<String> getOptions() {
        if (options == null)
            return null;

        return Collections.unmodifiableList(options);
    }
}
