package be.tobiridi.encoder.data;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class PaymentMethod implements Serializable {
    private static final long serialVersionUID = 24563238946569L;

    private String name;

    // --- Getters & Setters ---

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PaymentMethod(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "PaymentMethod{name='" + name + '\'' +'}';
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
