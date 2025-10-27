package be.tobiridi.encoder.saving;

import android.content.Context;
import android.util.JsonReader;
import android.util.JsonWriter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;

import be.tobiridi.encoder.R;

// TODO: 22/10/2025 maybe add cryptography algorithm to secure the json file "Android Key Store"
/**
 * Global class to manipulate a JSON file.
 * <br/>
 * For save the modification, call the method {@link JSONFileStorage#saveToFile()}.
 */
public final class JSONFileStorage {
    private static JSONFileStorage INSTANCE;
    private static final String FILE_NAME = "finance_prefs.json";
    private File jsonFile;
    private JSONObject fileData;

    private JSONFileStorage(Context ctx) throws IOException, JSONException {
        this.fileData = new JSONObject();
        this.createOrGetFile(ctx);
    }

    public static JSONFileStorage getInstance(Context ctx) throws IOException, JSONException {
        if (INSTANCE == null) {
            INSTANCE = new JSONFileStorage(ctx);
        }
        return INSTANCE;
    }

    /**
     * Create the JSON file if not exists, store its references, initialize it if first time
     * and load this content.
     * @param ctx The {@link Context} of the application.
     * @throws IOException If the file can not be created, written or read.
     * @throws JSONException If an error occurred while reading data from the JSON file.
     */
    private void createOrGetFile(Context ctx) throws IOException, JSONException {
        File file = new File(ctx.getFilesDir(), FILE_NAME);
        if (file.createNewFile()) {
            this.jsonFile = file;
            this.initJSONFile(ctx);
        }
        else if(file.isFile()) {
            this.jsonFile = file;
        }
        this.loadDataFromFile();
    }

    private void initJSONFile(Context ctx) throws IOException {
        try (JsonWriter writer = new JsonWriter(
                new BufferedWriter(new FileWriter(this.jsonFile))
        )) {
            String[] categories = {
                    ctx.getString(R.string.payment_cat_invoices),
                    ctx.getString(R.string.payment_cat_transports),
                    ctx.getString(R.string.payment_cat_rent),
                    ctx.getString(R.string.payment_cat_car),
                    ctx.getString(R.string.payment_cat_services),
                    ctx.getString(R.string.payment_cat_food),
                    ctx.getString(R.string.payment_cat_education),
                    ctx.getString(R.string.payment_cat_health),
                    ctx.getString(R.string.payment_cat_hobbies),
            };

            String[] paymentMethods = {
                    ctx.getString(R.string.payment_method_money),
                    ctx.getString(R.string.payment_method_bank_cbc),
                    ctx.getString(R.string.payment_method_bank_n26),
                    ctx.getString(R.string.payment_method_bank_revolut),
            };

            JSONEntry cat = JSONEntry.CATEGORIES;
            cat.setValue(categories);

            JSONEntry pay = JSONEntry.PAYMENT_METHODS;
            pay.setValue(paymentMethods);

            //write categories
            writer.beginObject();
            writer.name(cat.getKeyName());
            writer.beginArray();
            for (String catVal: cat.getValueAs(String[].class)) {
                writer.value(catVal);
            }
            writer.endArray();

            //write payment methods
            writer.name(pay.getKeyName());
            writer.beginArray();
            for (String payVal: pay.getValueAs(String[].class)) {
                writer.value(payVal);
            }
            writer.endArray();
            writer.endObject();

        } catch (IOException e) {
            throw e;
        }
    }

    private void loadDataFromFile() throws IOException, JSONException {
        try (JsonReader reader = new JsonReader(
                new BufferedReader(new FileReader(this.jsonFile))
        )) {
            JSONEntry cat = JSONEntry.CATEGORIES;
            JSONEntry payMethods = JSONEntry.PAYMENT_METHODS;

            reader.beginObject();
            while(reader.hasNext()) {
                String keyName = reader.nextName();

                //get categories key
                if (keyName.equalsIgnoreCase(cat.getKeyName())) {
                    JSONArray categoriesArray = new JSONArray();
                    reader.beginArray();
                    while (reader.hasNext()) {
                        categoriesArray.put(reader.nextString());
                    }
                    reader.endArray();
                    this.fileData.put(cat.getKeyName(), categoriesArray);
                }
                //get payment method key
                else if (keyName.equalsIgnoreCase(payMethods.getKeyName())) {
                    JSONArray payMethodsArray = new JSONArray();
                    reader.beginArray();
                    while (reader.hasNext()) {
                        payMethodsArray.put(reader.nextString());
                    }
                    reader.endArray();
                    this.fileData.put(payMethods.getKeyName(), payMethodsArray);

                } else {
                    // Ignore others keys if not use otherwise infinite loop
                    reader.skipValue();
                }
            }
            reader.endObject();

        } catch (IOException | JSONException e) {
            throw e;
        }
    }

    public boolean addCategory(String... newCategories) {
        JSONEntry catEntry = JSONEntry.CATEGORIES;

        if(newCategories != null) {
            try {
                JSONArray catArray = this.fileData.getJSONArray(catEntry.getKeyName());
                catEntry.setValue(newCategories);

                for (String category: catEntry.getValueAs(String[].class)) {
                    catArray.put(category);
                }
                return true;

            } catch (JSONException e) {
                return false;
            }
        }
        else {
            return false;
        }
    }

    public boolean deleteCategory(String... categories) {
        JSONEntry catEntry = JSONEntry.CATEGORIES;

        try {
            catEntry.setValue(categories);
            String[] deleteArray = catEntry.getValueAs(String[].class);
            JSONArray catArray = this.fileData.getJSONArray(catEntry.getKeyName());

            for (int i = 0; i < catArray.length(); i++) {
                String jsonString = catArray.getString(i);
                if (Arrays.stream(deleteArray).anyMatch(d -> d.equalsIgnoreCase(jsonString))) {
                    // array size changed, restart at the same index than last remove
                    catArray.remove(i);
                    i--;
                }
            }
            return true;

        } catch (JSONException e) {
            return false;
        }
    }

    public boolean addPaymentMethod(String... newPaymentMethods) {
        JSONEntry payEntry = JSONEntry.PAYMENT_METHODS;

        if(newPaymentMethods != null) {
            try {
                JSONArray payArray = this.fileData.getJSONArray(payEntry.getKeyName());
                payEntry.setValue(newPaymentMethods);

                for (String paymentMethod: payEntry.getValueAs(String[].class)) {
                    payArray.put(paymentMethod);
                }
                return true;

            } catch (JSONException e) {
                return false;
            }
        }
        else {
            return false;
        }
    }

    public boolean deletePaymentMethod(String... paymentMethods) {
        JSONEntry payEntry = JSONEntry.PAYMENT_METHODS;

        try {
            payEntry.setValue(paymentMethods);
            String[] deleteArray = payEntry.getValueAs(String[].class);
            JSONArray payArray = this.fileData.getJSONArray(payEntry.getKeyName());

            for (int i = 0; i < payArray.length(); i++) {
                String jsonString = payArray.getString(i);
                if (Arrays.stream(deleteArray).anyMatch(d -> d.equalsIgnoreCase(jsonString))) {
                    // array size changed, restart at the same index than last remove
                    payArray.remove(i);
                    i--;
                }
            }
            return true;

        } catch (JSONException e) {
            return false;
        }
    }

    /**
     * Apply the modification and rewrite the JSON file on the device.
     * @return {@code true} If the rewrite succeed, throw a {@link RuntimeException} otherwise.
     */
    public boolean saveToFile() {
        try (JsonWriter writer = new JsonWriter(
                new BufferedWriter(new FileWriter(this.jsonFile))
        )) {
            Iterator<String> jsonKeys = this.fileData.keys();

            writer.beginObject();
            while (jsonKeys.hasNext()) {
                String key = jsonKeys.next();
                writer.name(key);

                if (key.equalsIgnoreCase(JSONEntry.CATEGORIES.getKeyName()) ||
                    key.equalsIgnoreCase(JSONEntry.PAYMENT_METHODS.getKeyName())) {
                    JSONArray array = this.fileData.getJSONArray(key);
                    writer.beginArray();
                    for (int i = 0; i < array.length(); i++) {
                        writer.value(array.getString(i));
                    }
                    writer.endArray();
                }
            }
            writer.endObject();
            return true;

        } catch (IOException | JSONException e) {
            //normally never thrown, except if attempt to write wrong JSON
            throw new RuntimeException(e);
        }
    }

}
