package be.tobiridi.passwordsecurity.data.datasources.local;

import android.content.SharedPreferences;
import android.util.Base64;

import java.security.GeneralSecurityException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;

import be.tobiridi.passwordsecurity.data.database.AppDatabase;
import be.tobiridi.passwordsecurity.data.database.Dao.UserPreferencesDao;
import be.tobiridi.passwordsecurity.data.entities.UserPreferences;
import be.tobiridi.passwordsecurity.data.security.AESManager;
import be.tobiridi.passwordsecurity.data.security.HashManager;
import be.tobiridi.passwordsecurity.ui.fragments.settings.SettingsPreferenceKey;

/**
 * The local data source class for manipulate the authentication of the user.
 */
public final class AuthenticationLocalDataSource implements LocalDataSource {
    //private static final byte DEFAULT_MAX_AUTH_ATTEMPTS = 3;
    private final SharedPreferences sharedPreferences;
    private final UserPreferencesDao userPreferencesDao;
    private byte[] masterPassword;
    private boolean isAuthenticate;

    public AuthenticationLocalDataSource(SharedPreferences sharedPref, AppDatabase appDatabase) {
        this.sharedPreferences = sharedPref;
        this.userPreferencesDao = appDatabase.getUserPreferencesDao();

        this.isAuthenticate = false;
        this.masterPassword = new byte[0];
    }

    public boolean isUserAuthenticate() {
        return this.isAuthenticate;
    }

    public byte getMaxAuthAttempts() {
        return (byte) sharedPreferences.getInt(SettingsPreferenceKey.MAX_AUTH_ATTEMPTS, 3);
    }

    /**
     * Get the master password to access at the app.
     * @return The master password to encrypt and decrypt data.
     */
    public byte[] getMasterPassword() {
        return this.masterPassword;
    }

    /**
     * Check if the master password of the app exists.
     * @return {@code true} If the master password exists, {@code false} if the master password does not exist.
     */
    public boolean hasMasterPassword() {
        //return null if not found
        return this.userPreferencesDao.getMasterPassword() != null;
    }

    /**
     * Save a new master password to authenticate the user.
     * <br/>
     * It will be replace if the master password already exists.
     * @param newMasterPassword The user master password.
     * @return The rowId of new row saved.
     */
    public long saveMasterPassword(String newMasterPassword) {
        byte[] masterPassword = HashManager.hashStringToBytes(newMasterPassword);

        try {
            String encryptedMasterPassword = AESManager.encryptToStringBase64(masterPassword, masterPassword);

            //save the master password for reuse it in the app
            this.masterPassword = masterPassword;

            UserPreferences pref = new UserPreferences();
            pref.setMasterPassword(encryptedMasterPassword);
            //if no master password is set then create a new one, otherwise update the master password
            if (this.hasMasterPassword())
                return this.userPreferencesDao.updateUserPreferences(pref);
            else
                return this.userPreferencesDao.insertUserPreferences(pref);

        } catch (GeneralSecurityException e) {
            //never happened because encrypt the key with the same key
            return 0L;
        }
    }

    /**
     * Attempt to authenticate the user with the provided password.
     * @param userPassword The user password.
     * @return {@code true} If the password matches {@code false} otherwise.
     */
    public boolean authenticateUser(String userPassword) {
            byte[] masterPassword = HashManager.hashStringToBytes(userPassword);
            String encryptedMasterPassword = this.userPreferencesDao.getMasterPassword();

            try {
                String decryptedMasterPassword = AESManager.decryptToStringBase64(masterPassword, encryptedMasterPassword);
                this.isAuthenticate = Base64.encodeToString(masterPassword, Base64.DEFAULT).equals(decryptedMasterPassword);

                if (this.isAuthenticate) {
                    //save the master password for reuse it in the app
                    this.masterPassword = masterPassword;
                }
                return this.isAuthenticate;

            } catch (BadPaddingException e) {
                //the user password is wrong
                return false;
            } catch (GeneralSecurityException e) {
                //the key used for decryption is different than encryption
                return false;
            }
    }

    public void clearAllData() {
        this.sharedPreferences.edit()
                .clear()
                .apply();
        AppDatabase.clearAllTablesFromDatabase();
        Arrays.fill(this.masterPassword, (byte) 0);
        this.masterPassword = new byte[0];
        this.isAuthenticate = false;
    }
}
