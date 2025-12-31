package be.tobiridi.passwordsecurity.ui.updateAccount;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.EnumSet;

import be.tobiridi.passwordsecurity.R;
import be.tobiridi.passwordsecurity.component.TextWatcherResetError;
import be.tobiridi.passwordsecurity.component.accountField.AccountField;
import be.tobiridi.passwordsecurity.component.accountField.AccountFieldInputLayout;
import be.tobiridi.passwordsecurity.data.Account;

public class UpdateAccountActivity extends AppCompatActivity {
    private UpdateAccountViewModel updateAccountViewModel;
    private TextInputLayout accountNameInputLayout, accountPasswordInputLayout;
    private ArrayList<TextInputLayout> accountFields;
    private EnumSet<AccountField> addedFields;
    private ConstraintLayout updateAccountLayout;
    private Button validateBtn;
    private Account updateAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update_account);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.layout_updateAccount), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = this.getIntent();
        this.updateAccount = (Account) intent.getSerializableExtra("updatableAccount");

        //get views id
        this.accountNameInputLayout = this.findViewById(R.id.accountField_name);
        this.accountPasswordInputLayout = this.findViewById(R.id.accountField_password);
        this.updateAccountLayout = this.findViewById(R.id.layout_updateAccount);
        this.validateBtn = this.findViewById(R.id.btn_validate);

        //set inputs value
        this.accountNameInputLayout.getEditText().setText(this.updateAccount.getName());
        this.accountPasswordInputLayout.getEditText().setText(this.updateAccount.getPassword());

        //init the fields already existing
        this.addedFields = EnumSet.of(AccountField.NAME, AccountField.PASSWORD);
        this.accountFields = new ArrayList<>();
        this.accountFields.add(this.accountNameInputLayout);
        this.accountFields.add(this.accountPasswordInputLayout);
        this.generateAccountInputFields();

        //init viewModel
        this.updateAccountViewModel = new ViewModelProvider(this).get(UpdateAccountViewModel.class);
        this.updateAccountViewModel
                .setContext(this.getApplicationContext())
                .setUpdatableAccount(this.updateAccount);

        this.initListeners();
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
        this.accountNameInputLayout.getEditText().addTextChangedListener(new TextWatcherResetError(this.accountNameInputLayout));
        this.accountPasswordInputLayout.getEditText().addTextChangedListener(new TextWatcherResetError(this.accountPasswordInputLayout));

        this.validateBtn.setOnClickListener(v -> {
            if (this.updateAccountViewModel.updateAccount(this.accountFields, this.addedFields)) {
                this.finish();
            }
        });
    }

    private void generateAccountInputFields() {
        if (this.updateAccount.getEmail() != null)
            this.addedFields.add(AccountField.EMAIL);
        if (this.updateAccount.getUsername() != null)
            this.addedFields.add(AccountField.USERNAME);
        if (this.updateAccount.getNote() != null)
            this.addedFields.add(AccountField.NOTE);

        // generate views
        for (AccountField accountField : this.addedFields) {
            // fields already existing in the layout
            if (accountField.equals(AccountField.NAME) || accountField.equals(AccountField.PASSWORD)) {
                continue;
            }

            // update the index if the layout change
            int lastInputChildIndex = this.updateAccountLayout.getChildCount() - 2;
            View lastChildInput = this.updateAccountLayout.getChildAt(lastInputChildIndex);
            AccountFieldInputLayout customField = new AccountFieldInputLayout(this, accountField);

            LinearLayout parentLayout = customField.getParentLayout();
            var parentLayoutParams = parentLayout.getLayoutParams();
            if (parentLayoutParams instanceof ConstraintLayout.LayoutParams) {
                ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) parentLayoutParams;
                params.topToBottom = lastChildInput.getId();
                params.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
                params.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
            }

            String text;
            switch (accountField) {
                case EMAIL: text = this.updateAccount.getEmail();
                    break;
                case USERNAME: text = this.updateAccount.getUsername();
                    break;
                case NOTE: text = this.updateAccount.getNote();
                    break;
                default: text = null;
                    break;
            }
            customField.getEditText().setText(text);

            customField.getDeleteButton().setOnClickListener(v -> this.deleteInputField(customField, accountField));
            this.updateAccountLayout.addView(customField.getParentLayout(), lastInputChildIndex + 1);
            this.accountFields.add(customField);
        }
    }

    private void deleteInputField(AccountFieldInputLayout customField, AccountField accountField) {
        int removeIndex = this.updateAccountLayout.indexOfChild(customField.getParentLayout());
        // update the next custom view position
        // only move if the next child is a custom TextInputLayout
        // change the condition if the layout associates to this activity changed
        if (!(removeIndex + 1 >= this.updateAccountLayout.getChildCount() - 1)) {
            View previousChild = this.updateAccountLayout.getChildAt(removeIndex - 1);
            View nextChild = this.updateAccountLayout.getChildAt(removeIndex + 1);
            ViewGroup.LayoutParams nextChildLayoutParams = nextChild.getLayoutParams();
            if (nextChildLayoutParams instanceof ConstraintLayout.LayoutParams) {
                ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) nextChildLayoutParams;
                params.topToBottom = previousChild.getId();
                nextChild.setLayoutParams(params);
            }
        }
        // don't move the next view because you delete the last custom view
        // the previous view is a XML hardcoded TextInputLayout
        this.updateAccountLayout.removeViewAt(removeIndex);
        this.addedFields.remove(accountField);
        this.accountFields.remove(customField);
    }
}