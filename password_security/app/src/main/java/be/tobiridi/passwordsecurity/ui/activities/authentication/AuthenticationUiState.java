package be.tobiridi.passwordsecurity.ui.activities.authentication;

import androidx.annotation.StringRes;

/**
 * Represent the UI state for user authentication.
 * @see AuthenticationActivity
 * @see AuthenticationViewModel
 */
public final class AuthenticationUiState {
    private final String userPassword;
    private final String userConfirmPassword;
    private final boolean isLogin;
    private final boolean hasErrors;
    @StringRes
    private final Integer errorMessage;
    private final boolean maxAuthAttemptsReached;

    public AuthenticationUiState(String userPassword, String userConfirmPassword, boolean isLogin, @StringRes Integer errorMessage, boolean maxAuthAttemptsReached) {
        this.userPassword = userPassword;
        this.userConfirmPassword = userConfirmPassword;
        this.isLogin = isLogin;
        this.errorMessage = errorMessage;
        this.hasErrors = errorMessage != null;
        this.maxAuthAttemptsReached = maxAuthAttemptsReached;
    }

    public String getUserPassword() {
        return this.userPassword;
    }

    public String getUserConfirmPassword() {
        return this.userConfirmPassword;
    }

    public boolean isLogin() {
        return this.isLogin;
    }

    public boolean hasErrors() {
        return this.hasErrors;
    }

    @StringRes
    public int getErrorMessage() {
        return this.errorMessage;
    }

    public boolean isMaxAuthAttemptsReached() {
        return this.maxAuthAttemptsReached;
    }
}
