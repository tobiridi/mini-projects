package be.tobiridi.passwordsecurity.ui.updateAccount;

import android.content.Context;
import android.content.res.Resources;
import android.util.Patterns;

import androidx.lifecycle.ViewModel;

import com.google.android.material.textfield.TextInputLayout;

import be.tobiridi.passwordsecurity.R;
import be.tobiridi.passwordsecurity.data.Account;
import be.tobiridi.passwordsecurity.data.AccountDataSource;

public class UpdateAccountViewModel extends ViewModel {
    private AccountDataSource accountDataSource;
    private Resources resources;
    private Account updatableAccount;

    public UpdateAccountViewModel() {}

    /*********************/
    /** Builder methods **/
    /*********************/

    /**
     * Builder method.
     * @param ctx {@link Context}.
     * @return The same reference.
     */
    public UpdateAccountViewModel setContext(Context ctx) {
        this.resources = ctx.getResources();
        this.accountDataSource = AccountDataSource.getInstance(ctx);
        return this;
    }

    /**
     * Builder method.
     * @param updatableAccount The updatable {@link Account}.
     * @return The same reference.
     */
    public UpdateAccountViewModel setUpdatableAccount(Account updatableAccount) {
        this.updatableAccount = updatableAccount;
        return this;
    }

    public boolean updateAccount(TextInputLayout accountName, TextInputLayout accountEmail, TextInputLayout accountPwd) {
        int errors = 0;

        String name = accountName.getEditText().getText().toString();
        String email = accountEmail.getEditText().getText().toString();
        String pwd = accountPwd.getEditText().getText().toString();

        if (!this.isNameValid(accountName)) errors++;
        if (!this.isEmailValid(accountEmail)) errors++;
        if (!this.isPasswordValid(accountPwd)) errors++;

        if (errors == 0) {
            this.updatableAccount.setName(name);
            this.updatableAccount.setEmail(email);
            this.updatableAccount.setPassword(pwd);
            this.updatableAccount.packAccountData();
            return this.accountDataSource.updateAccount(this.updatableAccount) > 0;
        }
        return false;
    }

    private boolean isNameValid(TextInputLayout accountName) {
        String name = accountName.getEditText().getText().toString();

        if (name.trim().isEmpty()) {
            accountName.setError(this.resources.getString(R.string.error_account_name_empty));
            return false;
        }
        return true;
    }

    private boolean isEmailValid(TextInputLayout accountEmail) {
        String email = accountEmail.getEditText().getText().toString();

        if (email.trim().isEmpty()) {
            accountEmail.setError(this.resources.getString(R.string.error_account_email_empty));
            return false;
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            accountEmail.setError(this.resources.getString(R.string.error_account_email_format));
            return false;
        }
        return true;
    }

    private boolean isPasswordValid(TextInputLayout accountPwd) {
        String pwd = accountPwd.getEditText().getText().toString();

        if (pwd.trim().isEmpty()) {
            accountPwd.setError(this.resources.getString(R.string.error_account_password_empty));
            return false;
        }
        return true;
    }

}
