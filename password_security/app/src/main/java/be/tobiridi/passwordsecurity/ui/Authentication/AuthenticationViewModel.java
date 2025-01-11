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

    public AuthenticationViewModel(Context context) {
        this._userPreferencesDataSource = UserPreferencesDataSource.getInstance(context);
    }

    public boolean confirmPassword(String password) {
        if (!password.isEmpty()) {
            String hashPwd = HashManager.hashStringToStringBase64(password);
            System.out.println("input password : " + hashPwd);
            return this._userPreferencesDataSource.authenticateUser(hashPwd);
        }

        return false;
    }

}
