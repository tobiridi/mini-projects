package be.tobiridi.passwordsecurity.component.accountField;

import android.content.Context;

import androidx.appcompat.view.ContextThemeWrapper;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import be.tobiridi.passwordsecurity.R;
import be.tobiridi.passwordsecurity.component.TextWatcherResetError;
import be.tobiridi.passwordsecurity.data.Account;

/**
 * Main class for all dynamic custom {@link TextInputLayout} associated to an {@link Account}.
 * @see AccountField
 */
public class AccountFieldInputLayout extends TextInputLayout {
    private final AccountField accountField;

    public AccountFieldInputLayout(Context ctx, AccountField accountField) {
        // apply the material3 style
        super(new ContextThemeWrapper(ctx, R.style.TextInputLayoutOutlinedBox));
        this.accountField = accountField;
        this.setId(this.accountField.getId());
        this.setHint(this.accountField.getLabel());

        // apply the material3 style
        TextInputEditText inputEditText = new TextInputEditText(new ContextThemeWrapper(this.getContext(), R.style.TextInputEditTextOutlinedBox));
        inputEditText.setInputType(this.accountField.getInputType());
        inputEditText.addTextChangedListener(new TextWatcherResetError(this));

        this.addView(inputEditText);
    }

    public AccountField getAccountField() {
        return this.accountField;
    }
}