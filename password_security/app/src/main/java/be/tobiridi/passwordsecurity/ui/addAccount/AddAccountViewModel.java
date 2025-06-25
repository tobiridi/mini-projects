package be.tobiridi.passwordsecurity.ui.addAccount;

import static androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.util.Patterns;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.viewmodel.ViewModelInitializer;

import com.google.android.material.textfield.TextInputLayout;

import java.util.EnumSet;
import java.util.List;

import be.tobiridi.passwordsecurity.R;
import be.tobiridi.passwordsecurity.component.accountField.AccountField;
import be.tobiridi.passwordsecurity.data.Account;
import be.tobiridi.passwordsecurity.data.AccountDataSource;

public class AddAccountViewModel extends ViewModel {
    /*********************/
    /* ViewModel Factory */
    /*********************/
    public static final ViewModelInitializer<AddAccountViewModel> initializer = new ViewModelInitializer<>(
            AddAccountViewModel.class,
            creationExtras -> {
                Application app = creationExtras.get(APPLICATION_KEY);
                assert app != null;

                return new AddAccountViewModel(app.getApplicationContext());
            }
    );

    private final AccountDataSource _accountDataSource;
    private final Resources _resources;

    public AddAccountViewModel(Context context) {
        this._accountDataSource = AccountDataSource.getInstance(context);
        this._resources = context.getResources();
    }

    /**
     * Attempt to save the new account in the database.
     *
     * @param inputAccountFields All account fields input.
     * @param accountFields      All fields used to create an {@link Account}.
     * @return {@code true} is the account has been save in the database, {@code false} if an error has occurred.
     * @throws IllegalArgumentException If the number of elements present in each parameter is not the same.
     */
    public boolean createAccount(List<TextInputLayout> inputAccountFields, EnumSet<AccountField> accountFields) throws IllegalArgumentException {
        long[] idResults = {};

        // normally never throw except if forget to add one or more AccountField in the EnumSet
        if (inputAccountFields.size() != accountFields.size()) {
            throw new IllegalArgumentException("The number of TextInputLayout is not the same than the number of AccountField.");
        }

        int errors = 0;
        for (AccountField field : accountFields) {
            TextInputLayout input = inputAccountFields.stream()
                    .filter(i -> i.getId() == field.getId())
                    .findFirst()
                    .orElse(null);

            // can be null if not use the same id
            if (input != null) {
                switch (field) {
                    case NAME:
                        if (!isNameValid(input)) errors++;
                        break;
                    case PASSWORD:
                        if (!this.isPasswordValid(input)) errors++;
                        break;
                    case EMAIL:
                        if (!this.isEmailValid(input)) errors++;
                        break;
                    case USERNAME:
                        if (!this.isUsernameValid(input)) errors++;
                        break;
                    case NOTE:
                        if (!this.isNoteValid(input)) errors++;
                        break;
                }
            }
        }

        // TODO: if no errors, create account
        if (errors == 0) {
//            String accName = accountName.getEditText().getText().toString();
//            String accEmail = accountEmail.getEditText().getText().toString();
//            String accPassword = accountPassword.getEditText().getText().toString();
//            LocalDateTime created = LocalDateTime.now();
//
//            Account a = new Account(accName, accEmail, accPassword, created, created);
//            idResults = this._accountDataSource.saveAccounts(a);
        }

        return idResults.length > 0;
    }

    /******************/
    /** Account Fields
     validations  **/
    /******************/

    /**
     * Check if the account name is in the right format.
     *
     * @param nameInput The account name input.
     * @return {@code true} if the account name has a valid format.
     */
    private boolean isNameValid(TextInputLayout nameInput) {
        String txt = nameInput.getEditText().getText().toString();

        if (txt.trim().isEmpty()) {
            nameInput.setError(this._resources.getString(R.string.error_account_name_empty));
            return false;
        }
        return true;
    }

    /**
     * Check if the account password input is in the right format.
     *
     * @param passwordInput The account password input.
     * @return {@code true} if the account password has a valid format.
     */
    private boolean isPasswordValid(TextInputLayout passwordInput) {
        String txt = passwordInput.getEditText().getText().toString();

        if (txt.trim().isEmpty()) {
            passwordInput.setError(this._resources.getString(R.string.error_account_password_empty));
            return false;
        }
        return true;
    }

    /**
     * Check if the account email is in the right format.
     *
     * @param emailInput The account email input.
     * @return {@code true} if the account email has a valid format.
     */
    private boolean isEmailValid(TextInputLayout emailInput) {
        String txt = emailInput.getEditText().getText().toString();

        if (txt.trim().isEmpty()) {
            emailInput.setError(this._resources.getString(R.string.error_account_email_empty));
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(txt).matches()) {
            emailInput.setError(this._resources.getString(R.string.error_account_email_format));
            return false;
        }
        return true;
    }

    /**
     * Check if the account username is in the right format.
     *
     * @param usernameInput The account username input.
     * @return {@code true} if the account username has a valid format.
     */
    private boolean isUsernameValid(TextInputLayout usernameInput) {
        String txt = usernameInput.getEditText().getText().toString();

        if (txt.trim().isEmpty()) {
            usernameInput.setError(this._resources.getString(R.string.error_account_username_empty));
            return false;
        }
        return true;
    }

    /**
     * Check if the account note is in the right format.
     *
     * @param noteInput The account note input.
     * @return {@code true} if the account note has a valid format.
     */
    private boolean isNoteValid(TextInputLayout noteInput) {
        String txt = noteInput.getEditText().getText().toString();

        if (txt.trim().isEmpty()) {
            noteInput.setError(this._resources.getString(R.string.error_account_note_empty));
            return false;
        }
        return true;
    }

}