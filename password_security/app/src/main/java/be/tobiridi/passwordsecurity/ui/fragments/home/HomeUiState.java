package be.tobiridi.passwordsecurity.ui.fragments.home;

import java.util.List;

import be.tobiridi.passwordsecurity.data.entities.Account;

/**
 * Represent the UI state for home view.
 * @see HomeFragment
 * @see HomeViewModel
 */
public final class HomeUiState {
    private final List<Account> decryptedAccounts;
    private final boolean isLoading;
    private final boolean isFiltering;
    private final String searchText;

    public HomeUiState(List<Account> decryptedAccounts, boolean isLoading, String searchText, boolean isFiltering) {
        this.decryptedAccounts = decryptedAccounts;
        this.isLoading = isLoading;
        this.searchText = searchText;
        this.isFiltering = isFiltering;
    }

    public List<Account> getDecryptedAccounts() {
        return this.decryptedAccounts;
    }

    public boolean isLoading() {
        return this.isLoading;
    }

    public boolean isFiltering() {
        return this.isFiltering;
    }

    public String getSearchText() {
        return this.searchText;
    }

    public int accountsCount() {
        return this.decryptedAccounts.size();
    }
}
