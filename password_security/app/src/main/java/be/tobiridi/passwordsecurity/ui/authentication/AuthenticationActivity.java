package be.tobiridi.passwordsecurity.ui.authentication;

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
import com.google.android.material.textfield.TextInputLayout;

import be.tobiridi.passwordsecurity.MainActivity;
import be.tobiridi.passwordsecurity.R;

public class AuthenticationActivity extends AppCompatActivity {
    private AuthenticationViewModel authViewModel;
    private Button validateBtn;
    private TextInputLayout masterPasswordInputLayout, confirmMasterPasswordInputLayout;

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
        this.masterPasswordInputLayout = this.findViewById(R.id.inputLayout_masterPassword);
        this.confirmMasterPasswordInputLayout = this.findViewById(R.id.inputLayout_confirmMasterPassword);

        this.initListeners();
    }

    private void initListeners() {
        //reuse the same button with different actions
        if (this.authViewModel.isMasterPasswordExists()) {
            this.validateBtn.setOnClickListener(this.validateAuthenticationListener());
        }
        else {
            this.validateBtn.setOnClickListener(this.validateAuthenticationCreationListener());
            this.confirmMasterPasswordInputLayout.getEditText().addTextChangedListener(this.confirmPasswordWatcher());
        }

        this.masterPasswordInputLayout.getEditText().addTextChangedListener(this.passwordWatcher());
    }

    private View.OnClickListener validateAuthenticationListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = masterPasswordInputLayout.getEditText().getText().toString();
                if (authViewModel.confirmPassword(password)) {
                    Intent intent = new Intent(AuthenticationActivity.this, MainActivity.class);
                    startActivity(intent);
                    AuthenticationActivity.this.finish();
                }
                else {
                    String error = v.getResources().getString(R.string.error_password_not_same);
                    masterPasswordInputLayout.setError(error);

                    //destroy all data if max attempts is reached
                    if (authViewModel.isMaxAuthAttemptReached()) {
                        authViewModel.destroyAllData(AuthenticationActivity.this.getApplicationContext());
                        AuthenticationActivity.this.finish();
                    }
                }
            }
        };
    }

    private View.OnClickListener validateAuthenticationCreationListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = masterPasswordInputLayout.getEditText().getText().toString();
                String confirmPassword = confirmMasterPasswordInputLayout.getEditText().getText().toString();

                if (!authViewModel.isPasswordEqualsConfirmPassword(password, confirmPassword)) {
                    String error = v.getResources().getString(R.string.error_password_not_same);
                    confirmMasterPasswordInputLayout.setError(error);
                    return;
                }

                if (authViewModel.createMasterPassword(password)) {
                    Intent intent = new Intent(AuthenticationActivity.this, MainActivity.class);
                    startActivity(intent);
                    AuthenticationActivity.this.finish();
                }
                else {
                    masterPasswordInputLayout.getEditText().onEditorAction(EditorInfo.IME_ACTION_DONE);
                    confirmMasterPasswordInputLayout.getEditText().onEditorAction(EditorInfo.IME_ACTION_DONE);
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
                if (confirmMasterPasswordInputLayout != null) {
                    String confirmPwd = confirmMasterPasswordInputLayout.getEditText().getText().toString();
                    if (!authViewModel.isPasswordEqualsConfirmPassword(s.toString(), confirmPwd)) {
                        String error = AuthenticationActivity.this.getResources().getString(R.string.error_password_not_same);
                        confirmMasterPasswordInputLayout.setError(error);
                    }
                    else {
                        confirmMasterPasswordInputLayout.setError(null);
                    }

                    return;
                }

                masterPasswordInputLayout.setError(null);
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
                String password = masterPasswordInputLayout.getEditText().getText().toString();

                if (authViewModel.isPasswordEqualsConfirmPassword(password, s.toString())) {
                    confirmMasterPasswordInputLayout.setError(null);
                }
                else {
                    String error = AuthenticationActivity.this.getResources().getString(R.string.error_password_not_same);
                    confirmMasterPasswordInputLayout.setError(error);
                }
            }
        };
    }
}