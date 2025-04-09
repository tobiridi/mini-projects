package be.tobiridi.passwordsecurity.ui.authentication;

import static androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.viewmodel.ViewModelInitializer;
import androidx.preference.PreferenceManager;

import be.tobiridi.passwordsecurity.data.UserPreferencesDataSource;

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
    private byte authAttempt;
    private final byte _maxAuthAttempt;

    public AuthenticationViewModel(Context context) {
        this._userPreferencesDataSource = UserPreferencesDataSource.getInstance(context);
        this.authAttempt = 0;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        //retrieve from Preference, otherwise set to 3
        this._maxAuthAttempt = (byte) prefs.getInt("attempts", 3);
    }

    public boolean isMasterPasswordExists() {
        if (this.hasMasterPassword == null) {
            this.hasMasterPassword = this._userPreferencesDataSource.hasMasterPassword();
        }
        return this.hasMasterPassword;
    }

    public boolean isMaxAuthAttemptReached() {
        //if max attempt is set to 0 == unlimited
        if (this._maxAuthAttempt == 0) {
            return false;
        }
        return this.authAttempt >= this._maxAuthAttempt;
    }

    public boolean confirmPassword(String masterPassword) {
        if (!masterPassword.isEmpty()) {
            this.authAttempt++;
            return this._userPreferencesDataSource.authenticateUser(masterPassword);
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
            return this._userPreferencesDataSource.saveMasterPassword(masterPwd);
        }
        return false;
    }

    public void destroyAllData(Context ctx) {
        this._userPreferencesDataSource.destroyAllData(ctx);
    }

}
