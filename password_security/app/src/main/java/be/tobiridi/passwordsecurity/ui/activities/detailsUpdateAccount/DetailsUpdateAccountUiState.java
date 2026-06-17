package be.tobiridi.passwordsecurity.ui.activities.detailsUpdateAccount;

/**
 * Represent the UI state for details and update an account.
 * @see DetailsUpdateAccountActivity
 * @see DetailsUpdateAccountViewModel
 */
public final class DetailsUpdateAccountUiState {
    private final boolean editMode;

    public DetailsUpdateAccountUiState(boolean editMode) {
        this.editMode = editMode;
    }

    public boolean isInEditMode() {
        return this.editMode;
    }
}
