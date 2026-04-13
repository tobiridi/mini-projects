package be.tobiridi.passwordsecurity.ui.fragments.addAccount;

import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;

import be.tobiridi.passwordsecurity.R;
import be.tobiridi.passwordsecurity.data.entities.Account;
import be.tobiridi.passwordsecurity.ui.components.TextWatcherResetError;
import be.tobiridi.passwordsecurity.ui.components.accountField.AccountField;
import be.tobiridi.passwordsecurity.ui.components.accountField.AccountFieldInputLayout;

public class AddAccountFragment extends Fragment {
    private AddAccountViewModel addAccountViewModel;
    private TextInputLayout accountNameInputLayout, accountPasswordInputLayout;
    private Button resetBtn, validateBtn;
    private FloatingActionButton addFieldFloatBtn;
    private ConstraintLayout addAccountLayout;

    public static AddAccountFragment newInstance() {
        return new AddAccountFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //get views id
        this.accountNameInputLayout = view.findViewById(R.id.accountField_name);
        this.accountPasswordInputLayout = view.findViewById(R.id.accountField_password);
        this.resetBtn = view.findViewById(R.id.btn_reset);
        this.validateBtn = view.findViewById(R.id.btn_validate);
        this.addFieldFloatBtn = view.findViewById(R.id.floatBtn_add_field);
        this.addAccountLayout = view.findViewById(R.id.layout_addAccount);

        this.addAccountViewModel = new ViewModelProvider(this, ViewModelProvider.Factory.from(AddAccountViewModel.initializer)).get(AddAccountViewModel.class);

        this.addAccountViewModel.getAddAccountUiState().observe(this.getViewLifecycleOwner(), (AddAccountUiState uiState) -> {
            uiState.getDisplayAccountFields().stream()
                    .filter(af -> this.addAccountLayout.findViewById(af.getId()) == null)
                    .forEach(this::addDynamicField);

            if (uiState.isResetForm()) {
                this.resetUiForm(uiState.getDisplayAccountFields());
            }
            //common statement between created and hasErrors
            else if (uiState.isAccountCreated() || uiState.hasErrors()) {
                Snackbar.make(this.requireContext(), this.requireView(), this.getString(uiState.getMessage()), Snackbar.LENGTH_SHORT)
                        .setAnimationMode(Snackbar.ANIMATION_MODE_FADE)
                        .setAnchorView(R.id.bottomNavigationView)
                        .show();
            }

            if (uiState.isAccountCreated()) {
                this.resetBtn.callOnClick();
            }
        });

        this.initListeners();
    }

    private void initListeners() {
        this.resetBtn.setOnClickListener(v -> this.addAccountViewModel.resetForm());
        this.validateBtn.setOnClickListener(v -> this.validateUiForm());
        this.addFieldFloatBtn.setOnClickListener(v -> showFieldSelectionDialog());
        this.accountNameInputLayout.getEditText().addTextChangedListener(new TextWatcherResetError(this.accountNameInputLayout));
        this.accountPasswordInputLayout.getEditText().addTextChangedListener(new TextWatcherResetError(this.accountPasswordInputLayout));
    }

    private void resetUiForm(Iterable<AccountField> accountFields) {
        accountFields.forEach(f -> {
            TextInputLayout input = this.addAccountLayout.findViewById(f.getId());
            input.setError(null);
            input.getEditText().getText().clear();
            //close soft keyboard
            input.getEditText().onEditorAction(EditorInfo.IME_ACTION_DONE);
        });
    }

    private void validateUiForm() {
        //check if all input layout are valid
        int errors = 0;
        if(!this.isUiNameValid())
            errors++;
        if(!this.isUiPasswordValid())
            errors++;
        if(!this.isUiEmailValid())
            errors++;
        if(!this.isUiUsernameValid())
            errors++;
        if(!this.isUiNoteValid())
            errors++;

        if (errors == 0) {
            TextInputLayout inputName = this.addAccountLayout.findViewById(AccountField.NAME.getId());
            TextInputLayout inputPwd = this.addAccountLayout.findViewById(AccountField.PASSWORD.getId());
            TextInputLayout inputEmail = this.addAccountLayout.findViewById(AccountField.EMAIL.getId());
            TextInputLayout inputUsername = this.addAccountLayout.findViewById(AccountField.USERNAME.getId());
            TextInputLayout inputNote = this.addAccountLayout.findViewById(AccountField.NOTE.getId());

            String name = inputName.getEditText().getText().toString();
            String password = inputPwd.getEditText().getText().toString();
            //optional inputs
            String email = inputEmail == null ? null : inputEmail.getEditText().getText().toString();
            String username = inputUsername == null ? null : inputUsername.getEditText().getText().toString();
            String note = inputNote == null ? null : inputNote.getEditText().getText().toString();

            Account createAccount = new Account(name, password, email, username, note);
            this.addAccountViewModel.createAccount(createAccount);
        }
    }

    /******************/
    /** Account Fields
     validations  **/
    /******************/

    /**
     * Check if the account name is in the right format.
     *
     * @return {@code true} if the account name has a valid format.
     */
    private boolean isUiNameValid() {
        TextInputLayout input = this.addAccountLayout.findViewById(AccountField.NAME.getId());
        if (input == null) {
            return true;
        }

        String txt = input.getEditText().getText().toString();
        if (txt.isBlank()) {
            input.setError(this.getResources().getString(R.string.error_account_name_empty));
            return false;
        }
        return true;
    }

    /**
     * Check if the account password input is in the right format.
     *
     * @return {@code true} if the account password has a valid format.
     */
    private boolean isUiPasswordValid() {
        TextInputLayout input = this.addAccountLayout.findViewById(AccountField.PASSWORD.getId());
        if (input == null) {
            return true;
        }

        String txt = input.getEditText().getText().toString();
        if (txt.isBlank()) {
            input.setError(this.getResources().getString(R.string.error_account_password_empty));
            return false;
        }
        return true;
    }

    /**
     * Check if the account email is in the right format.
     *
     * @return {@code true} if the account email has a valid format.
     */
    private boolean isUiEmailValid() {
        TextInputLayout input = this.addAccountLayout.findViewById(AccountField.EMAIL.getId());
        if (input == null) {
            return true;
        }

        String txt = input.getEditText().getText().toString();
        if (txt.isBlank()) {
            input.setError(this.getResources().getString(R.string.error_account_email_empty));
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(txt).matches()) {
            input.setError(this.getResources().getString(R.string.error_account_email_format));
            return false;
        }
        return true;
    }

    /**
     * Check if the account username is in the right format.
     *
     * @return {@code true} if the account username has a valid format.
     */
    private boolean isUiUsernameValid() {
        TextInputLayout input = this.addAccountLayout.findViewById(AccountField.USERNAME.getId());
        if (input == null) {
            return true;
        }

        String txt = input.getEditText().getText().toString();
        if (txt.isBlank()) {
            input.setError(this.getResources().getString(R.string.error_account_username_empty));
            return false;
        }
        return true;
    }

    /**
     * Check if the account note is in the right format.
     *
     * @return {@code true} if the account note has a valid format.
     */
    private boolean isUiNoteValid() {
        TextInputLayout input = this.addAccountLayout.findViewById(AccountField.NOTE.getId());
        if (input == null) {
            return true;
        }

        String txt = input.getEditText().getText().toString();
        if (txt.isBlank()) {
            input.setError(this.getResources().getString(R.string.error_account_note_empty));
            return false;
        }
        return true;
    }

    private void showFieldSelectionDialog() {
        List<AccountField> remainingFields = this.addAccountViewModel.getRemainingFields();

        String[] items = remainingFields.stream()
                .map(field -> getResources().getString(field.getLabel()))
                .toArray(String[]::new);

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.add_field)
                .setItems(items, (dialog, which) -> {
                    AccountField selected = remainingFields.get(which);

                    if (remainingFields.size() == 1) {
                        this.addFieldFloatBtn.setEnabled(false);
                    }
                    this.addAccountViewModel.addAccountField(selected);
                })
                .show();
    }

    private void addDynamicField(AccountField field) {
        // update the index if the layout associates to this fragment changed
        // get the index of the last input field
        int lastInputChildIndex = this.addAccountLayout.getChildCount() - 3;
        View lastChildInput = this.addAccountLayout.getChildAt(lastInputChildIndex);
        AccountFieldInputLayout customInputLayout = new AccountFieldInputLayout(this.requireContext(), field);

        // adapt the layout params for the custom TextInputLayout, move it below to the previous input field
        LinearLayout parentLayout = customInputLayout.getParentLayout();
        ViewGroup.LayoutParams parentLayoutParams = parentLayout.getLayoutParams();
        if (parentLayoutParams instanceof ConstraintLayout.LayoutParams) {
            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) parentLayoutParams;
            params.topToBottom = lastChildInput.getId();
            params.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
            params.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
        }

        customInputLayout.getDeleteButton().setOnClickListener(v -> this.removeDynamicField(customInputLayout, field));

        //position the custom TextInputLayout below the last TextInputLayout
        this.addAccountLayout.addView(customInputLayout.getParentLayout(), lastInputChildIndex + 1);
    }

    private void removeDynamicField(AccountFieldInputLayout customInputLayout, AccountField field) {
        int removeIndex = this.addAccountLayout.indexOfChild(customInputLayout.getParentLayout());
        // update the next custom view position
        // change the condition if the layout associates to this fragment changed
        // only move if the next child is a custom TextInputLayout
        if (!(removeIndex + 1 >= this.addAccountLayout.getChildCount() - 2)) {
            View previousChild = this.addAccountLayout.getChildAt(removeIndex - 1);
            View nextChild = this.addAccountLayout.getChildAt(removeIndex + 1);
            ViewGroup.LayoutParams nextChildLayoutParams = nextChild.getLayoutParams();
            if (nextChildLayoutParams instanceof ConstraintLayout.LayoutParams) {
                ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) nextChildLayoutParams;
                params.topToBottom = previousChild.getId();
                nextChild.setLayoutParams(params);
            }
        }
        // don't move the next view because you delete the last custom view
        // the previous view is a XML hardcoded TextInputLayout
        this.addAccountLayout.removeViewAt(removeIndex);
        this.addAccountViewModel.removeAccountField(field);
        this.addFieldFloatBtn.setEnabled(true);
    }
}