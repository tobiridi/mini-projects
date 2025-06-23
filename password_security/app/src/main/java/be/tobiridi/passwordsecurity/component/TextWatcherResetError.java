package be.tobiridi.passwordsecurity.component;

import android.text.Editable;
import android.text.TextWatcher;

import com.google.android.material.textfield.TextInputLayout;

/**
 * Simple {@link TextWatcher} implementation to reset error message to a {@link TextInputLayout}.
 */
public class TextWatcherResetError implements TextWatcher {
    private TextInputLayout textInputLayout;

    public TextWatcherResetError(TextInputLayout textInput) {
        this.textInputLayout = textInput;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        //do nothing
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        this.textInputLayout.setError(null);
    }

    @Override
    public void afterTextChanged(Editable s) {
        //do nothing
    }
}
