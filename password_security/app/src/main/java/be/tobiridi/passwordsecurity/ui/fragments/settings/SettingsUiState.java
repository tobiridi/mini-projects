package be.tobiridi.passwordsecurity.ui.fragments.settings;

/**
 * Represent the UI state for settings view.
 * @see SettingsFragment
 * @see SettingsViewModel
 */
public final class SettingsUiState {
    boolean isAutomationActive;
    boolean isNotificationActive;

    public SettingsUiState(boolean isAutomationActive, boolean isNotificationActive) {
        this.isAutomationActive = isAutomationActive;
        this.isNotificationActive = isNotificationActive;
    }

    public boolean isAutomationActive() {
        return this.isAutomationActive;
    }

    public boolean isNotificationActive() {
        return this.isNotificationActive;
    }
}
