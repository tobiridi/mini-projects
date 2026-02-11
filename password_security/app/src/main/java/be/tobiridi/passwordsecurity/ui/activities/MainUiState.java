package be.tobiridi.passwordsecurity.ui.activities;

import androidx.fragment.app.Fragment;

/**
 * Represent the UI state for main activity.
 * @see MainActivity
 * @see MainViewModel
 */
public final class MainUiState {
    private final Fragment previousFragDisplay;
    private final Fragment currentFragDisplay;
    private final boolean isFragManagerInit;

    public MainUiState(Fragment previousFragDisplay, Fragment currentFragDisplay, boolean isFragManagerInit) {
        this.previousFragDisplay = previousFragDisplay;
        this.currentFragDisplay = currentFragDisplay;
        this.isFragManagerInit = isFragManagerInit;
    }

    public Fragment getPreviousFragDisplay() {
        return this.previousFragDisplay;
    }

    public Fragment getCurrentFragDisplay() {
        return this.currentFragDisplay;
    }

    public boolean isFragManagerInit() {
        return this.isFragManagerInit;
    }
}
