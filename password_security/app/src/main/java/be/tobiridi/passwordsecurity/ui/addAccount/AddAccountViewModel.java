package be.tobiridi.passwordsecurity.ui.addAccount;

import static androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.util.Patterns;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.viewmodel.ViewModelInitializer;

import com.google.android.material.textfield.TextInputLayout;

import java.time.LocalDateTime;

import be.tobiridi.passwordsecurity.R;
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
     * @param accountName The account name input.
     * @param accountEmail The account email input.
     * @param accountPassword The account password input.
     * @return {@code true} is the account has been save in the database, {@code false} if an error has occurred.
     */
    public boolean createAccount(TextInputLayout accountName, TextInputLayout accountEmail, TextInputLayout accountPassword) {
        int errors = 0;
        long[] idResults = {};

        if (!this.isNameValid(accountName)) errors++;
        if (!this.isEmailValid(accountEmail)) errors++;
        if (!this.isPasswordValid(accountPassword)) errors++;

        if (errors == 0) {
            String accName = accountName.getEditText().getText().toString();
            String accEmail = accountEmail.getEditText().getText().toString();
            String accPassword = accountPassword.getEditText().getText().toString();
            LocalDateTime created = LocalDateTime.now();

            Account a = new Account(accName, accEmail, accPassword, created, created);
            idResults = this._accountDataSource.saveAccounts(a);
        }

        return idResults.length > 0;
    }

    /**
     * Check if the account name input is in the right format.
     * @param name The account name input.
     * @return {@code true} if the account name has a valid format.
     */
    private boolean isNameValid(TextInputLayout name) {
        String txt = name.getEditText().getText().toString();

        if (txt.trim().isEmpty()) {
            name.setError(this._resources.getString(R.string.error_account_name_empty));
            return false;
        }
        return true;
    }

    /**
     * Check if the account email input is in the right format.
     * @param email The account email input.
     * @return {@code true} if the account email has a valid format.
     */
    private boolean isEmailValid(TextInputLayout email) {
        String txt = email.getEditText().getText().toString();

        if (txt.trim().isEmpty()) {
            email.setError(this._resources.getString(R.string.error_account_email_empty));
            return false;
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(txt).matches()) {
            email.setError(this._resources.getString(R.string.error_account_email_format));
            return false;
        }
        return true;
    }

    /**
     * Check if the account password input is in the right format.
     * @param password The account password input.
     * @return {@code true} if the account password has a valid format.
     */
    private boolean isPasswordValid(TextInputLayout password) {
        String txt = password.getEditText().getText().toString();

        if (txt.trim().isEmpty()) {
            password.setError(this._resources.getString(R.string.error_account_password_empty));
            return false;
        }
        return true;
    }

}