package be.tobiridi.passwordsecurity.ui.fragments.addAccount;

import androidx.annotation.StringRes;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

import be.tobiridi.passwordsecurity.data.entities.Account;
import be.tobiridi.passwordsecurity.ui.components.accountField.AccountField;

public final class AddAccountUiState {
    private final EnumSet<AccountField> displayAccountFields;
    @StringRes
    private final int message;
    private final boolean hasErrors;
    private final boolean isResetForm;
    private final Account createdAccount;

    public AddAccountUiState(EnumSet<AccountField> displayAccountFields, @StringRes int message, boolean hasErrors, boolean isResetForm, Account createdAccount) {
        this.displayAccountFields = displayAccountFields;
        this.message = message;
        this.isResetForm = isResetForm;
        this.createdAccount = createdAccount;
        this.hasErrors = hasErrors;
    }

    public EnumSet<AccountField> getDisplayAccountFields() {
        return this.displayAccountFields;
    }

    public boolean isResetForm() {
        return this.isResetForm;
    }

    public boolean isAccountCreated() {
        return this.createdAccount != null;
    }

    public boolean hasErrors() {return this.hasErrors;}

    @StringRes
    public int getMessage() {
        return this.message;
    }

    public Account getCreatedAccount() {
        return this.createdAccount;
    }

    public List<AccountField> getRemainingFields() {
        return Arrays.stream(AccountField.values())
                .filter(f -> !this.displayAccountFields.contains(f))
                .collect(Collectors.toList());
    }
}
