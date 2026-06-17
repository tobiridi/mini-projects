package be.tobiridi.passwordsecurity.ui.activities.authentication;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import be.tobiridi.passwordsecurity.R;
import be.tobiridi.passwordsecurity.ui.activities.MainActivity;

public class AuthenticationActivity extends AppCompatActivity {
    private AuthenticationViewModel authViewModel;
    private Button validateBtn;
    private TextInputLayout masterPasswordInputLayout, confirmMasterPasswordInputLayout;
    private ConstraintLayout authLayout;

    /*************/
    /* Listeners */
    /*************/
    private final View.OnClickListener validateAuthenticationListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String password = masterPasswordInputLayout.getEditText().getText().toString();

            if (!authViewModel.login(password)) {
                String error = v.getResources().getString(R.string.error_password_not_same);
                masterPasswordInputLayout.setError(error);
            }
        }
    };

    private final View.OnClickListener validateAuthenticationCreationListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String password = masterPasswordInputLayout.getEditText().getText().toString();
            String confirmPassword = confirmMasterPasswordInputLayout.getEditText().getText().toString();
            masterPasswordInputLayout.getEditText().onEditorAction(EditorInfo.IME_ACTION_DONE);
            confirmMasterPasswordInputLayout.getEditText().onEditorAction(EditorInfo.IME_ACTION_DONE);

            if (!confirmPassword.equals(password)) {
                String error = v.getResources().getString(R.string.error_password_not_same);
                confirmMasterPasswordInputLayout.setError(error);
                return;
            }

            authViewModel.createMasterPassword(password);
        }
    };

    /***************/
    /* TextWatcher */
    /***************/
    private final TextWatcher passwordWatcher = new TextWatcher() {
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

                if (!s.toString().equals(confirmPwd)) {
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

    private final TextWatcher confirmPasswordWatcher = new TextWatcher() {
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

            if (password.equals(s.toString())) {
                confirmMasterPasswordInputLayout.setError(null);
            }
            else {
                String error = AuthenticationActivity.this.getResources().getString(R.string.error_password_not_same);
                confirmMasterPasswordInputLayout.setError(error);
            }
        }
    };

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

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.layout_auth), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //get views id
        this.validateBtn = this.findViewById(R.id.btn_validate);
        this.authLayout = this.findViewById(R.id.layout_auth);
        this.masterPasswordInputLayout = this.findViewById(R.id.inputLayout_masterPassword);
        this.confirmMasterPasswordInputLayout = this.findViewById(R.id.inputLayout_confirmMasterPassword);

        this.initObservers();
        this.initListeners();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.authViewModel.getAuthUiState().removeObservers(this);
    }

    private void initObservers() {
        this.authViewModel.getAuthUiState().observe(this, (AuthenticationUiState uiState) -> {
            if (uiState.isMaxAuthAttemptsReached()) {
                authViewModel.destroyAllData();
                AuthenticationActivity.this.finish();
            }
            else if(uiState.isLogin()) {
                Intent intent = new Intent(AuthenticationActivity.this, MainActivity.class);
                startActivity(intent);
                AuthenticationActivity.this.finish();
            }
            else {
                this.masterPasswordInputLayout.getEditText().setText(uiState.getUserPassword());
                //can be null if load the layout to authenticate the user
                if(this.confirmMasterPasswordInputLayout != null) {
                    this.confirmMasterPasswordInputLayout.getEditText().setText(uiState.getUserConfirmPassword());
                }

                if (uiState.hasErrors()) {
                    Snackbar.make(this.authLayout, this.getString(uiState.getErrorMessage()), Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    private void initListeners() {
        //reuse the same button with different actions
        if (this.authViewModel.isMasterPasswordExists()) {
            this.validateBtn.setOnClickListener(this.validateAuthenticationListener);
        }
        else {
            this.validateBtn.setOnClickListener(this.validateAuthenticationCreationListener);
            this.confirmMasterPasswordInputLayout.getEditText().addTextChangedListener(this.confirmPasswordWatcher);
        }

        this.masterPasswordInputLayout.getEditText().addTextChangedListener(this.passwordWatcher);
    }
}