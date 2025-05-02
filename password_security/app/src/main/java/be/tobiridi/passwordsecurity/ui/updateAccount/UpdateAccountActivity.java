package be.tobiridi.passwordsecurity.ui.updateAccount;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputLayout;

import be.tobiridi.passwordsecurity.R;
import be.tobiridi.passwordsecurity.component.TextWatcherResetError;
import be.tobiridi.passwordsecurity.data.Account;

public class UpdateAccountActivity extends AppCompatActivity {
    private UpdateAccountViewModel updateAccountViewModel;
    private View.OnClickListener validateListener;
    private TextInputLayout accountNameInputLayout, accountEmailInputLayout, accountPasswordInputLayout;
    private Button validateBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update_account);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //get views id
        this.accountNameInputLayout = this.findViewById(R.id.inputLayout_accountName);
        this.accountEmailInputLayout = this.findViewById(R.id.inputLayout_accountEmail);
        this.accountPasswordInputLayout = this.findViewById(R.id.inputLayout_accountPassword);
        this.validateBtn = this.findViewById(R.id.btn_validate);

        //init viewModel
        Intent intent = this.getIntent();
        Account updateAccount = (Account) intent.getSerializableExtra("updatableAccount");
        this.updateAccountViewModel = new ViewModelProvider(this).get(UpdateAccountViewModel.class);
        this.updateAccountViewModel
                .setContext(this.getApplicationContext())
                .setUpdatableAccount(updateAccount);

        //set inputs value
        this.accountNameInputLayout.getEditText().setText(updateAccount.getName());
        this.accountEmailInputLayout.getEditText().setText(updateAccount.getEmail());
        this.accountPasswordInputLayout.getEditText().setText(updateAccount.getPassword());

        this.initListeners();
        this.validateBtn.setOnClickListener(this.validateListener);
        this.accountNameInputLayout.getEditText().addTextChangedListener(new TextWatcherResetError(this.accountNameInputLayout));
        this.accountEmailInputLayout.getEditText().addTextChangedListener(new TextWatcherResetError(this.accountEmailInputLayout));
        this.accountPasswordInputLayout.getEditText().addTextChangedListener(new TextWatcherResetError(this.accountPasswordInputLayout));
    }

    @Override
    protected void onStop() {
        super.onStop();
        PowerManager manager = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
        if (!manager.isInteractive()) {
            this.finishAffinity();
        }
    }

    private void initListeners() {
        this.validateListener = (view) -> {
            if (this.updateAccountViewModel.updateAccount(this.accountNameInputLayout,
                    this.accountEmailInputLayout, this.accountPasswordInputLayout)) {
                this.finish();
            }
        };
    }
}