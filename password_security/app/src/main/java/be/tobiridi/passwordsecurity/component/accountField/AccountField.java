package be.tobiridi.passwordsecurity.component.accountField;

import android.text.InputType;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.IdRes;
import androidx.annotation.StringRes;

import com.google.android.material.textfield.TextInputLayout;

import be.tobiridi.passwordsecurity.R;
import be.tobiridi.passwordsecurity.data.Account;

/**
 * Custom field as a data for an {@link Account}, should be used with a {@link TextInputLayout}.
 * @see AccountFieldInputLayout
 */
public enum AccountField {
    EMAIL(R.id.accountField_email, R.string.account_field_email, InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS, R.string.hint_email_example),
    USERNAME(R.id.accountField_username, R.string.account_field_username, InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE, R.string.hint_username_example),
    NOTE(R.id.accountField_note, R.string.account_field_note, InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE, R.string.hint_note);

    @IdRes
    private final int id;
    @StringRes
    private final int label;
    /**
     * constant as defined for {@link EditorInfo#inputType}.
     */
    private final int inputType;
    @StringRes
    private final int hintText;

    private AccountField(@IdRes int id, @StringRes int label, int inputType, @StringRes int hintText) {
        this.id = id;
        this.label = label;
        this.inputType = inputType;
        this.hintText = hintText;
    }

    @IdRes
    public int getId() {
        return id;
    }

    @StringRes
    public int getLabel() {
        return label;
    }

    public int getInputType() {
        return this.inputType;
    }

    @StringRes
    public int getHintText() {
        return this.hintText;
    }
}
