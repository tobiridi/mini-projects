package be.tobiridi.passwordsecurity.ui.Authentication;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import be.tobiridi.passwordsecurity.MainActivity;
import be.tobiridi.passwordsecurity.R;

public class AuthenticationActivity extends AppCompatActivity {
    private AuthenticationViewModel authViewModel;
    private Button validateBtn;
    private TextInputLayout passwordInputLayout, confirmPasswordInputLayout;
    private TextInputEditText passwordInputEditText, confirmPasswordInputEditText;
    private byte authAttempt = 0, maxAuthAttempt = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        this.authViewModel = new ViewModelProvider(this, ViewModelProvider.Factory.from(AuthenticationViewModel.initializer)).get(AuthenticationViewModel.class);

        //change layout if the master password has been set previously
        if (this.authViewModel.isMasterPasswordExists()) {
            setContentView(R.layout.activity_authentication);
        }
        else {
            setContentView(R.layout.activity_authentication_creation);
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //get views id
        this.validateBtn = this.findViewById(R.id.btn_validate);
        this.passwordInputLayout = this.findViewById(R.id.inputLayout_password);
        this.passwordInputEditText = this.findViewById(R.id.inputEditText_password);
        this.confirmPasswordInputLayout = this.findViewById(R.id.inputLayout_confirm_password);
        this.confirmPasswordInputEditText = this.findViewById(R.id.inputEditText_confirm_password);

        this.initListeners();
    }

    private void initListeners() {
        //reuse the same button with different actions
        if (this.authViewModel.isMasterPasswordExists()) {
            this.validateBtn.setOnClickListener(this.validateAuthenticationListener());
        }
        else {
            this.validateBtn.setOnClickListener(this.validateAuthenticationCreationListener());
            this.confirmPasswordInputEditText.addTextChangedListener(this.confirmPasswordWatcher());
        }

        this.passwordInputEditText.addTextChangedListener(this.passwordWatcher());
    }

    private View.OnClickListener validateAuthenticationListener() {
        return new View.OnClickListener() {
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

                    //destroy all data if max attempts is reached
                    AuthenticationActivity.this.authAttempt++;
                    if (AuthenticationActivity.this.authAttempt == AuthenticationActivity.this.maxAuthAttempt) {
                        authViewModel.destroyAllData(AuthenticationActivity.this.getApplicationContext());
                    }
                }
            }
        };
    }

    private View.OnClickListener validateAuthenticationCreationListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = passwordInputEditText.getText().toString();
                String confirmPassword = confirmPasswordInputEditText.getText().toString();

                if (!authViewModel.isPasswordEqualsConfirmPassword(password, confirmPassword)) {
                    String error = v.getResources().getString(R.string.error_password_not_same);
                    confirmPasswordInputLayout.setError(error);
                    return;
                }

                if (authViewModel.createMasterPassword(password)) {
                    Intent intent = new Intent(AuthenticationActivity.this, MainActivity.class);
                    startActivity(intent);
                    AuthenticationActivity.this.finish();
                }
                else {
                    passwordInputEditText.onEditorAction(EditorInfo.IME_ACTION_DONE);
                    confirmPasswordInputEditText.onEditorAction(EditorInfo.IME_ACTION_DONE);
                    String error = v.getResources().getString(R.string.error_master_password_creation);
                    Snackbar.make(v, error, Snackbar.LENGTH_LONG).show();
                }
            }
        };
    }

    private TextWatcher passwordWatcher() {
        return new TextWatcher() {
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
                //null if the master password exists, because layout changed to "login"
                if (confirmPasswordInputEditText != null) {
                    String confirmPwd = confirmPasswordInputEditText.getText().toString();
                    if (!authViewModel.isPasswordEqualsConfirmPassword(s.toString(), confirmPwd)) {
                        String error = AuthenticationActivity.this.getResources().getString(R.string.error_password_not_same);
                        confirmPasswordInputLayout.setError(error);
                    }
                    else {
                        confirmPasswordInputLayout.setError(null);
                    }

                    return;
                }

                passwordInputLayout.setError(null);
            }
        };
    }

    private TextWatcher confirmPasswordWatcher() {
        return new TextWatcher() {
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
                String password = passwordInputEditText.getText().toString();

                if (authViewModel.isPasswordEqualsConfirmPassword(password, s.toString())) {
                    confirmPasswordInputLayout.setError(null);
                }
                else {
                    String error = AuthenticationActivity.this.getResources().getString(R.string.error_password_not_same);
                    confirmPasswordInputLayout.setError(error);
                }
            }
        };
    }
}