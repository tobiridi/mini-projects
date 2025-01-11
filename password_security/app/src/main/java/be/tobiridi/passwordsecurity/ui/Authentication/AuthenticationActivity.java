package be.tobiridi.passwordsecurity.ui.Authentication;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import be.tobiridi.passwordsecurity.MainActivity;
import be.tobiridi.passwordsecurity.R;

public class AuthenticationActivity extends AppCompatActivity {
    private AuthenticationViewModel authViewModel;
    private Button validateBtn;
    private TextInputLayout passwordInputLayout;
    private TextInputEditText passwordInputEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_authentication);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        this.authViewModel = new ViewModelProvider(this, ViewModelProvider.Factory.from(AuthenticationViewModel.initializer)).get(AuthenticationViewModel.class);

        //get views id
        this.validateBtn = this.findViewById(R.id.btn_validate);
        this.passwordInputLayout = this.findViewById(R.id.inputLayout_password);
        this.passwordInputEditText = this.findViewById(R.id.inputEditText_password);

        this.initListeners();
    }

    private void initListeners() {
        this.validateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    String password = passwordInputEditText.getText().toString();
                    if (authViewModel.confirmPassword(password)) {
                        Intent intent = new Intent(AuthenticationActivity.this, MainActivity.class);
                        startActivity(intent);
                        AuthenticationActivity.this.finish();
                    }
                    else {
                        String error = v.getResources().getString(R.string.error_password_not_same);
                        passwordInputLayout.setError(error);
                    }
            }
        });

        this.passwordInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //do nothing
            }

            @Override
            public void afterTextChanged(Editable s) {
                passwordInputLayout.setError(null);
            }
        });
    }
}