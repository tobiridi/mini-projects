package be.tobiridi.passwordsecurity.data.repositories;

import androidx.lifecycle.Observer;

import be.tobiridi.passwordsecurity.data.datasources.local.AuthenticationLocalDataSource;
import be.tobiridi.passwordsecurity.data.entities.UserPreferences;

/**
 * Centralize for the ui layer how {@link UserPreferences} entity can be manipulate.
 */
public class UserPreferencesRepository {
    private static AuthenticationLocalDataSource authDataSource;
    private static byte[] AUTH_MASTER_PASSWORD;
    private static Observer<byte[]> observerMasterPassword;

    private UserPreferencesRepository(AuthenticationLocalDataSource dataSource) {
        if(authDataSource == null) {
            authDataSource = dataSource;
            this.initObservers();
            authDataSource.getMasterPassword().observeForever(observerMasterPassword);
        }
    }

    private void initObservers() {
        observerMasterPassword = (byte[] bytes) -> AUTH_MASTER_PASSWORD = bytes;
    }

    private void removeAllObservers() {
        authDataSource.getMasterPassword().removeObserver(observerMasterPassword);
    }

    /**
     * Get the master password to access at the app.
     * @return The master password if the user is authenticate, an empty array otherwise.
     * @see #authenticateUser(String)
     */
    public byte[] getMasterPassword() {
        if (authDataSource.isUserAuthenticate()) {
            return AUTH_MASTER_PASSWORD;
        }
        return new byte[0];
    }

    /**
     * Determine if a master password has already set to access at the app.
     * @return {@code true} If already set, {@code false} otherwise.
     */
    public boolean isMasterPasswordAlreadySet() {
        return authDataSource.hasMasterPassword();
    }

    public boolean saveMasterPassword(String newMasterPwd) {
        if (newMasterPwd.trim().isEmpty())
            return false;

        return authDataSource.saveMasterPassword(newMasterPwd) > 0;
    }

    public boolean authenticateUser(String masterPwd) {
        if (masterPwd.trim().isEmpty())
            return false;

        if (authDataSource.isUserAuthenticate())
            return true;

        return authDataSource.authenticateUser(masterPwd);
    }
}
