package be.tobiridi.passwordsecurity.ui.Authentication;

import static androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.viewmodel.ViewModelInitializer;

import be.tobiridi.passwordsecurity.data.UserPreferencesDataSource;
import be.tobiridi.passwordsecurity.security.HashManager;

public class AuthenticationViewModel extends ViewModel {
    /*********************/
    /* ViewModel Factory */
    /*********************/
    public static final ViewModelInitializer<AuthenticationViewModel> initializer = new ViewModelInitializer<>(
            AuthenticationViewModel.class,
            creationExtras -> {
                Application app = creationExtras.get(APPLICATION_KEY);
                assert app != null;

                return new AuthenticationViewModel(app.getApplicationContext());
            }
    );

    private final UserPreferencesDataSource _userPreferencesDataSource;
    private Boolean hasMasterPassword;
    private boolean switchActivity;
    private byte authAttempt = 0;
    private final byte _maxAuthAttempt = 3;

    public AuthenticationViewModel(Context context) {
        this._userPreferencesDataSource = UserPreferencesDataSource.getInstance(context);
        this.switchActivity = false;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (!this.switchActivity) {
            this._userPreferencesDataSource.closeExecutorService();
        }
    }

    public boolean isMasterPasswordExists() {
        if (this.hasMasterPassword == null) {
            this.hasMasterPassword = this._userPreferencesDataSource.hasMasterPassword();
        }

        return this.hasMasterPassword;
    }

    public boolean isMaxAuthAttemptReached() {
        return this.authAttempt == this._maxAuthAttempt;
    }

    public boolean confirmPassword(String password) {
        if (!password.isEmpty()) {
            String hashPwd = HashManager.hashStringToStringBase64(password);
            if (this._userPreferencesDataSource.authenticateUser(hashPwd)) {
                this.switchActivity = true;
                return true;
            }
            this.authAttempt++;
        }
        return false;
    }

    public boolean isPasswordEqualsConfirmPassword(String password, String confirmPassword) {
        if (password.isEmpty() || confirmPassword.isEmpty()) {
            return false;
        }
        else {
            return confirmPassword.equals(password);
        }
    }

    public boolean createMasterPassword(String masterPwd) {
        if (!this.isMasterPasswordExists()) {
            String hashPwd = HashManager.hashStringToStringBase64(masterPwd);
            return this._userPreferencesDataSource.saveMasterPassword(hashPwd);
        }
        return false;
    }

    public void destroyAllData(Context ctx) {
        this._userPreferencesDataSource.destroyAllData(ctx);
    }

}
