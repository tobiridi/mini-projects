package be.tobiridi.passwordsecurity.component.accountField;

import android.content.Context;
import android.graphics.Color;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.appcompat.view.ContextThemeWrapper;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import be.tobiridi.passwordsecurity.R;
import be.tobiridi.passwordsecurity.component.TextWatcherResetError;
import be.tobiridi.passwordsecurity.data.Account;

/**
 * Main class for all dynamic custom {@link TextInputLayout} associated to an {@link Account}.
 * The data used for this {@link TextInputLayout} are defined as {@link AccountField} constant.
 * @see AccountField
 */
public class AccountFieldInputLayout extends TextInputLayout {
    private final AccountField accountField;
    private final ImageButton deleteButton;
    private final LinearLayout groupLayout;

    /**
     * Create a custom {@link TextInputLayout} with a {@link TextInputEditText} and a delete button {@link ImageButton}
     * grouped in a {@link LinearLayout}, also every views have a {@code LayoutParams}.
     * @param ctx The context for this view.
     * @param accountField A constant as defined for {@link AccountField}.
     */
    public AccountFieldInputLayout(Context ctx, AccountField accountField) {
        // apply the material3 style to the TextInputLayout
        super(new ContextThemeWrapper(ctx, R.style.TextInputLayoutOutlinedBox));
        this.accountField = accountField;
        LinearLayout.LayoutParams inputLayoutParams = new LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
        inputLayoutParams.setMarginEnd(this.pxToDp(8));
        inputLayoutParams.weight = 1f;
        this.setLayoutParams(inputLayoutParams);
        this.setId(this.accountField.getId());
        this.setHint(this.accountField.getLabel());

        // apply the material3 style to the TextInputEditText
        TextInputEditText inputEditText = new TextInputEditText(new ContextThemeWrapper(this.getContext(), R.style.TextInputEditTextOutlinedBox));
        LinearLayout.LayoutParams editTextLayoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        inputEditText.setInputType(this.accountField.getInputType());
        inputEditText.addTextChangedListener(new TextWatcherResetError(this));
        inputEditText.setLayoutParams(editTextLayoutParams);
        this.addView(inputEditText);

        this.deleteButton = new ImageButton(ctx);
        LinearLayout.LayoutParams iconLayoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        this.deleteButton.setLayoutParams(iconLayoutParams);
        this.deleteButton.setClickable(true);
        this.setContentDescription(ctx.getString(R.string.btn_desc_delete_field));
        this.deleteButton.setImageResource(R.drawable.baseline_remove_circle_outline_24);
        this.deleteButton.setBackgroundColor(Color.TRANSPARENT);

        // group the views in a LinearLayout
        this.groupLayout = new LinearLayout(ctx);
        ConstraintLayout.LayoutParams linearLayoutParams = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        linearLayoutParams.topMargin = this.pxToDp(16);
        this.groupLayout.setLayoutParams(linearLayoutParams);
        this.groupLayout.setOrientation(LinearLayout.HORIZONTAL);
        this.groupLayout.setId(ViewGroup.generateViewId());

        this.groupLayout.addView(this);
        this.groupLayout.addView(this.deleteButton);
    }

    /**
     * Convert the pixel to pixel densities.
     * @param px The number of pixel to convert.
     * @return The pixel densities value.
     */
    private int pxToDp(int px) {
        return Math.round(px * this.getResources().getDisplayMetrics().density);
    }

    /**
     * Get the parent container layout used to display the {@link AccountFieldInputLayout} with its button {@link ImageButton}.
     * @return The parent container.
     */
    public LinearLayout getParentLayout() {
        return this.groupLayout;
    }

    public AccountField getAccountField() {
        return this.accountField;
    }

    /**
     * Get the {@link ImageButton} associates to the {@link AccountFieldInputLayout}.
     * @return The delete button.
     */
    public ImageButton getDeleteButton() {
        return this.deleteButton;
    }
}