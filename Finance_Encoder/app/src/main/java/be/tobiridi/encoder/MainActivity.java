package be.tobiridi.encoder;

import android.os.Bundle;
import android.widget.ArrayAdapter;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.datepicker.MaterialDatePicker;

import be.tobiridi.encoder.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding activityMainBinding;
    private MainViewModel mainViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.activityMainBinding = ActivityMainBinding.inflate(this.getLayoutInflater());
        setContentView(this.activityMainBinding.getRoot());
        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        this.mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        //fill the dropdown menus
        if(this.mainViewModel.loadJsonFile(this)) {
            ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, R.layout.dropdown_item, R.id.textView_dropdown_item, this.mainViewModel.getCategories());
            this.activityMainBinding.dropdownCategory.setAdapter(categoryAdapter);

            ArrayAdapter<String> paymentAdapter = new ArrayAdapter<>(this, R.layout.dropdown_item, R.id.textView_dropdown_item, this.mainViewModel.getPaymentMethods());
            this.activityMainBinding.dropdownPaymentMethod.setAdapter(paymentAdapter);
        }

        this.initListener();
    }

    private void initListener() {
        // select dropdown value
        this.activityMainBinding.dropdownCategory.setOnItemClickListener((adapterView, view, position, id) -> {
            Object item = adapterView.getItemAtPosition(position);
            if(item instanceof String) {
                // TODO: 29/10/2025 store the item selected to viewModel
                String category = (String) item;
                System.out.println(item);
            }
        });

        this.activityMainBinding.dropdownPaymentMethod.setOnItemClickListener((adapterView, view, position, id) -> {
            Object item = adapterView.getItemAtPosition(position);
            if(item instanceof String) {
                // TODO: 29/10/2025 store the item selected to viewModel
                String paymentMethod = (String) item;
                System.out.println(item);
            }
        });

        // TODO: 29/10/2025 continue listener implementation

        // Sélection de la date et heure
//        this.activityMainBinding.editTextDate.setOnClickListener(v -> showDateTimePicker());
//
//        // Action du bouton
//        fabSave.setOnClickListener(v -> {
//            String name = editTextName.getText().toString();
//            String price = editTextPrice.getText().toString();
//            String date = editTextDate.getText().toString();
//
//            if (name.isEmpty() || price.isEmpty() || date.isEmpty()
//                    || selectedCategory == null || selectedPaymentMethod == null) {
//                Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
//                return;
//            }
//
//            Toast.makeText(this, "Paiement enregistré !", Toast.LENGTH_SHORT).show();
//        });
    }

    // TODO: 24-10-25 chat gpt generated code for layout
    private void showDateTimePicker() {
        //final Calendar calendar = Calendar.getInstance();

        MaterialDatePicker datePicker = new MaterialDatePicker();
//        this,
//                (view, year, month, dayOfMonth) -> {
//                    calendar.set(year, month, dayOfMonth);
//                    TimePickerDialog timePicker = new TimePickerDialog(this,
//                            (timeView, hour, minute) -> {
//                                calendar.set(Calendar.HOUR_OF_DAY, hour);
//                                calendar.set(Calendar.MINUTE, minute);
//
//                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
//                                editTextDate.setText(sdf.format(calendar.getTime()));
//                            },
//                            calendar.get(Calendar.HOUR_OF_DAY),
//                            calendar.get(Calendar.MINUTE),
//                            true);
//                    timePicker.show();
//                },
//                calendar.get(Calendar.YEAR),
//                calendar.get(Calendar.MONTH),
//                calendar.get(Calendar.DAY_OF_MONTH));
        //datePicker.show();
    }

    private void notionIntegration() {
        // TODO: 24-10-25 integration to Notion
        // https://developers.notion.com/reference/intro
        // https://developers.notion.com/reference/status-codes
        // HTTPS required, GET, POST, PATCH , DELETE, JSON Request, JSON Response
        // json_property always in "snake_case"
        // date and datetime format : 2020-08-12T02:12:33.231Z (ISO 8601)
        // empty string not supported, use an explicit null instead of "".
        /*
            JSON convention :
                // type of the resource (e.g. "database", "user", etc.)
                "object" : "",
                //a UUIDv4, You may omit dashes from the ID when making requests to the API, e.g. when copying the ID from a Notion URL.
                "id" : "",

         */
        String notionAPIURL = "https://api.notion.com/v1/pages";
        /* request example
            POST https://api.notion.com/v1/pages
            Authorization: Bearer secret_abCdEf123456789
            Content-Type: application/json
            Notion-Version: 2022-06-28
         */
    }

}