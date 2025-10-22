package be.tobiridi.encoder.saving;

import android.content.Context;
import android.util.JsonReader;
import android.util.JsonWriter;

import androidx.annotation.NonNull;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import be.jadoulle.encoder.R;

// TODO: 22/10/2025 make a better explanation
// TODO: 22/10/2025 maybe add cryptography algorithm to secure the json file "Android Key Store"
/**
 * Store a JSON file locally.
 */
public final class JSONFileStorage {
    private static JSONFileStorage INSTANCE;
    private static final String FILE_NAME = "finance_prefs.json";
    private boolean isFileChanged;
    private File JsonFile;

    private JSONFileStorage(Context ctx) throws IOException {
        // TODO: 22/10/2025 when file is changed, rewrite the json file before quit the app, call method
        this.isFileChanged = false;
        try {
            this.createOrGetFile(ctx);
        } catch (IOException e) {
            throw e;
        }
    }

    public static JSONFileStorage getInstance(Context ctx) throws IOException {
        if (INSTANCE == null) {
            INSTANCE = new JSONFileStorage(ctx);
        }
        return INSTANCE;
    }

    private void createOrGetFile(Context ctx) throws IOException {
        File file = new File(ctx.getFilesDir(), FILE_NAME);
        if (file.createNewFile()) {
            this.JsonFile = file;
            this.initJSONFile(ctx);
        }
        else if(file.isFile()) {
            this.JsonFile = file;
        }
    }

    private void initJSONFile(Context ctx) {
        try (JsonWriter writer = new JsonWriter(
                new BufferedWriter(new FileWriter(this.JsonFile))
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
            e.printStackTrace();
        }
    }

//    public boolean writeJson() {
//        try (JsonWriter writer = new JsonWriter(
//                new BufferedWriter(new FileWriter(this.JsonFile))
//        )) {
//            writer.beginObject();
//            writer.endObject();
//            return true;
//
//        } catch (IOException e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
//
//    public JSONObject readJson() {
//        try (JsonReader reader = new JsonReader(
//                new BufferedReader(new FileReader(this.JsonFile))
//        )) {
//            JSONObject jsonObject = new JSONObject();
//            reader.beginObject();
//            reader.endObject();
//            return jsonObject;
//
//        } catch (IOException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }

}
