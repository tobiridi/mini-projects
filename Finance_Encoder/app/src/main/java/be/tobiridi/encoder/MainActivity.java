package be.tobiridi.encoder;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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

    }
}