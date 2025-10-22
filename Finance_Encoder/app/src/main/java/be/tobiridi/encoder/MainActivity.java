package be.tobiridi.encoder;

import android.os.Bundle;
import android.util.JsonReader;
import android.util.JsonWriter;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

import be.jadoulle.encoder.R;
import be.tobiridi.encoder.data.Payment;
import be.tobiridi.encoder.data.PaymentCategory;
import be.tobiridi.encoder.data.PaymentMethod;
import be.tobiridi.encoder.saving.JSONEntry;
import be.tobiridi.encoder.saving.JSONFileStorage;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // TODO: 20/10/2025 use: navigation, databinding, theme, figma, json
        //store a JSON file in local
//        Payment pay1 = new Payment("Burger king", new BigDecimal("32.55"), OffsetDateTime.now(),
//                new PaymentCategory("Facture"), new PaymentMethod("money"));
//        JSONObject json = pay1.convertToJson();
//        System.out.println(json.toString());


        //create json file
//        try {
//            JSONFileStorage fileStorage = JSONFileStorage.getInstance(this.getApplicationContext());
//
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }

    }
}