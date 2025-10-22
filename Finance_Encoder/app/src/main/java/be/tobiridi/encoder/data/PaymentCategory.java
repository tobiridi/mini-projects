package be.tobiridi.encoder.data;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class PaymentCategory implements Serializable {
    private static final long serialVersionUID = 753826482198459L;
    private String name;

    // --- Getters & Setters ---

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PaymentCategory(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Category{name='" + name + '\'' + '}';
    }

    public JSONObject convertToJson() {
        JSONObject json = new JSONObject();
        try {
            json.put("name", this.name);

        } catch (JSONException e) {
            e.printStackTrace();
            return new JSONObject();
        }
        return json;
    }
}
