package be.tobiridi.passwordsecurity.ui.addAccount;

import static androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.util.Patterns;
import android.widget.EditText;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.viewmodel.ViewModelInitializer;

import java.time.LocalDate;

import be.tobiridi.passwordsecurity.R;
import be.tobiridi.passwordsecurity.data.Account;
import be.tobiridi.passwordsecurity.data.AccountDataSource;

public class AddAcountViewModel extends ViewModel {
    /*********************/
    /* ViewModel Factory */
    /*********************/
    public static final ViewModelInitializer<AddAcountViewModel> initializer = new ViewModelInitializer<>(
            AddAcountViewModel.class,
            creationExtras -> {
                Application app = creationExtras.get(APPLICATION_KEY);
                assert app != null;

                return new AddAcountViewModel(app.getApplicationContext());
            }
    );

    private final AccountDataSource _accountDataSource;
    private final Resources _resources;

    public AddAcountViewModel(Context context) {
        this._accountDataSource = AccountDataSource.getInstance(context);
        this._resources = context.getResources();
    }

    /**
     * Attempt to save the new account in the database.
     * @param accountName The account name input.
     * @param accountEmail The account email input.
     * @param accountPassword The account password input.
     * @return True is the account has been save in the database, false if an error has occurred.
     */
    public boolean createAccount(EditText accountName, EditText accountEmail, EditText accountPassword) {
        int errors = 0;
        long[] idResults = {};

        if (!this.isNameValid(accountName)) errors++;
        if (!this.isEmailValid(accountEmail)) errors++;
        if (!this.isPasswordValid(accountPassword)) errors++;

        if (errors == 0) {
            String name = accountName.getText().toString(),
                    email = accountEmail.getText().toString(),
                    pwd = accountPassword.getText().toString();

            LocalDate created = LocalDate.now();

            Account a = new Account(name, email, pwd, created, created);
            idResults = this._accountDataSource.saveAccount(a);
        }

        return idResults.length > 0;
    }

    /**
     * Check if the account name input is in the right format.
     * @param name The account name input.
     * @return True if it is valid format false otherwise.
     */
    private boolean isNameValid(EditText name) {
        String txt = name.getText().toString();

        if (txt.trim().isEmpty()) {
            name.setError(this._resources.getString(R.string.error_account_name_empty));
            return false;
        }

        return true;
    }

    /**
     * Check if the account email input is in the right format.
     * @param email The account email input.
     * @return True if it is valid format false otherwise.
     */
    private boolean isEmailValid(EditText email) {
        String txt = email.getText().toString();

        if (txt.trim().isEmpty()) {
            email.setError(this._resources.getString(R.string.error_account_email_empty));
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(txt).matches()) {
            email.setError(this._resources.getString(R.string.error_account_email_format));
            return false;
        }

        return true;
    }

    /**
     * Check if the account password input is in the right format.
     * @param password The account password input.
     * @return True if it is valid format false otherwise.
     */
    private boolean isPasswordValid(EditText password) {
        String txt = password.getText().toString();

        if (txt.trim().isEmpty()) {
            password.setError(this._resources.getString(R.string.error_account_password_empty));
            return false;
        }

        return true;
    }

}