package be.tobiridi.passwordsecurity.data.repositories;

import be.tobiridi.passwordsecurity.data.datasources.local.AuthenticationLocalDataSource;
import be.tobiridi.passwordsecurity.data.datasources.local.UserPreferencesLocalDataSource;
import be.tobiridi.passwordsecurity.data.entities.UserPreferences;

/**
 * Centralize for the ui layer how {@link UserPreferences} entity can be manipulate.
 */
public class UserPreferencesRepository {
    private static AuthenticationLocalDataSource authDataSource;
    private static UserPreferencesLocalDataSource userPrefsDataSource;

    public UserPreferencesRepository(AuthenticationLocalDataSource dataSource, UserPreferencesLocalDataSource dataSource2) {
        if(authDataSource == null) {
            authDataSource = dataSource;
        }
        if (userPrefsDataSource == null) {
            userPrefsDataSource = dataSource2;
        }
    }

    /**
     * Get the master password to access at the app.
     * @return The master password if the user is authenticate, an empty array otherwise.
     */
    public byte[] getMasterPassword() {
        if (authDataSource.isUserAuthenticate()) {
            return authDataSource.getMasterPassword();
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
        if (newMasterPwd.isBlank())
            return false;

        return authDataSource.saveMasterPassword(newMasterPwd) > 0;
    }

    public UserPreferences getUserPreferences() {
        return userPrefsDataSource.getUserPreferences();
    }

    public int updateUserPreferences(UserPreferences prefs) {
        return userPrefsDataSource.updateUserPreferences(prefs);
    }
}
