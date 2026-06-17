package be.tobiridi.passwordsecurity.ui.fragments.detailsAccount;

import be.tobiridi.passwordsecurity.data.entities.Account;

/**
 * Represent the UI state for DetailsAccount view.
 * @see DetailsAccountFragment
 * @see DetailsAccountViewModel
 */
public final class DetailsAccountUiState {
    private final Account account;

    public DetailsAccountUiState(Account account) {
        this.account = account;
    }

    public Account getAccount() {
        return this.account;
    }
}
