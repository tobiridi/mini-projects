package be.tobiridi.encoder.data;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;

public class Payment implements Serializable {
    private static final long serialVersionUID = 92285621473659L;

    private String name;
    private BigDecimal amount;
    private OffsetDateTime paymentDateTime;
    private PaymentCategory paymentCategory;
    private PaymentMethod paymentMethod;

    // --- Getters & Setters ---
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public OffsetDateTime getPaymentDateTime() {
        return paymentDateTime;
    }

    public void setPaymentDateTime(OffsetDateTime paymentDateTime) {
        this.paymentDateTime = paymentDateTime;
    }

    public PaymentCategory getCategory() {
        return paymentCategory;
    }

    public void setCategory(PaymentCategory paymentCategory) {
        this.paymentCategory = paymentCategory;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Payment(String name, BigDecimal amount, OffsetDateTime paymentDateTime, PaymentCategory paymentCategory, PaymentMethod paymentMethod) {
        this.name = name;
        this.amount = amount;
        this.paymentDateTime = paymentDateTime;
        this.paymentCategory = paymentCategory;
        this.paymentMethod = paymentMethod;
    }

    @Override
    public String toString() {
        return "Payment{" +
                "name='" + name + '\'' +
                ", amount=" + amount +
                ", paymentDateTime=" + paymentDateTime +
                ", category=" + paymentCategory +
                ", paymentMethod=" + paymentMethod +
                '}';
    }

    public JSONObject convertToJson() {
        JSONObject json = new JSONObject();
        this.amount = this.amount.setScale(2, RoundingMode.HALF_UP);
        try {
            json.put("name", this.name);
            json.put("amount", this.amount.toPlainString());
            json.put("paymentDateTime", this.paymentDateTime.toString());
            json.put("category", this.paymentCategory.convertToJson());
            json.put("paymentMethod", this.paymentMethod.convertToJson());

        } catch (JSONException e) {
            e.printStackTrace();
            return new JSONObject();
        }
        return json;
    }
}
