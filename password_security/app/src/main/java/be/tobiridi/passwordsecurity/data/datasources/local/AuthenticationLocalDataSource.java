package be.tobiridi.passwordsecurity.data.datasources.local;

import android.content.SharedPreferences;
import android.util.Base64;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.crypto.BadPaddingException;

import be.tobiridi.passwordsecurity.data.database.AppDatabase;
import be.tobiridi.passwordsecurity.data.database.Dao.UserPreferencesDao;
import be.tobiridi.passwordsecurity.data.entities.UserPreferences;
import be.tobiridi.passwordsecurity.data.security.AESManager;
import be.tobiridi.passwordsecurity.data.security.HashManager;
import be.tobiridi.passwordsecurity.data.utils.ExecutorServiceUtils;

/**
 * The local data source class for manipulate the authentication of the user.
 */
public final class AuthenticationLocalDataSource implements LocalDataSource {
    private static final byte DEFAULT_MAX_AUTH_ATTEMPTS = 3;
    public static final String MAX_AUTH_ATTEMPTS_KEY = "maxAuthAttempts";
    private static ExecutorService executorService;
    private final SharedPreferences sharedPreferences;
    private final UserPreferencesDao userPreferencesDao;
    private final MutableLiveData<Byte> mutableMaxAuthAttempts;
    private final MutableLiveData<byte[]> mutableMasterPassword;
    private boolean isAuthenticate;

    public AuthenticationLocalDataSource(SharedPreferences sharedPref, AppDatabase appDatabase) {
        if(executorService == null || executorService.isShutdown()) {
            executorService = Executors.newSingleThreadExecutor();
        }

        this.sharedPreferences = sharedPref;
        this.userPreferencesDao = appDatabase.getUserPreferencesDao();

        this.isAuthenticate = false;

        byte maxAuthAttempts = (byte) sharedPref.getInt(MAX_AUTH_ATTEMPTS_KEY, DEFAULT_MAX_AUTH_ATTEMPTS);
        this.mutableMaxAuthAttempts = new MutableLiveData<>(maxAuthAttempts);
        this.mutableMasterPassword = new MutableLiveData<>(new byte[0]);

        this.initListeners();
    }

    private void initListeners() {
        this.sharedPreferences.registerOnSharedPreferenceChangeListener((SharedPreferences prefs, @Nullable String key) -> {
            if (key != null) {
                if(key.equalsIgnoreCase(MAX_AUTH_ATTEMPTS_KEY)) {
                    byte authAttempts = (byte) prefs.getInt(MAX_AUTH_ATTEMPTS_KEY, DEFAULT_MAX_AUTH_ATTEMPTS);
                    //this.mutableMaxAuthAttempts.postValue(authAttempts);   //if in background
                    this.mutableMaxAuthAttempts.setValue(authAttempts);      //if in main thread
                }
            }
        });
    }

    public boolean isUserAuthenticate() {
        return this.isAuthenticate;
    }

    public LiveData<Byte> getMaxAuthAttempts() {
        return this.mutableMaxAuthAttempts;
    }

    /**
     * Get the master password to access at the app.
     * @return The master password to encrypt and decrypt data.
     */
    public LiveData<byte[]> getMasterPassword() {
        return this.mutableMasterPassword;
    }

    /**
     * Check if the master password of the app exists.
     * @return {@code true} If the master password exists, {@code false} if the master password does not exist.
     */
    public boolean hasMasterPassword() {
        Callable<Boolean> callable = () -> {
            //return null if not found
            return this.userPreferencesDao.getMasterPassword() != null;
        };
        return ExecutorServiceUtils.executeCallable(executorService, callable);
    }

    /**
     * Save a new master password to authenticate the user.
     * <br/>
     * It will be replace if the master password already exists.
     * @param newMasterPassword The user master password.
     * @return The number of rows affected.
     */
    public long saveMasterPassword(String newMasterPassword) {
        Callable<Long> callable = () -> {
            byte[] masterPassword = HashManager.hashStringToBytes(newMasterPassword);
            String encryptedMasterPassword = AESManager.encryptToStringBase64(masterPassword, masterPassword);

            //save the master password for reuse it in the app
            this.mutableMasterPassword.postValue(masterPassword);

            UserPreferences pref = new UserPreferences(encryptedMasterPassword);
            return this.userPreferencesDao.saveMasterPassword(pref);
        };
        return ExecutorServiceUtils.executeCallable(executorService, callable);
    }

    /**
     * Clear the master password from the memory.
     */
    public void clearMasterPasswordFromMemory() {
        Arrays.fill(this.mutableMasterPassword.getValue(), (byte) 0);
        this.mutableMasterPassword.setValue(new byte[0]);
        this.isAuthenticate = false;
    }

    /**
     * Attempt to authenticate the user with the provided password.
     * @param userPassword The user password.
     * @return {@code true} If the password matches {@code false} otherwise.
     */
    public boolean authenticateUser(String userPassword) {
        Callable<Boolean> callable = () -> {
            byte[] masterPassword = HashManager.hashStringToBytes(userPassword);
            String encryptedMasterPassword = this.userPreferencesDao.getMasterPassword();

            try {
                String decryptedMasterPassword = AESManager.decryptToStringBase64(masterPassword, encryptedMasterPassword);
                this.isAuthenticate = Base64.encodeToString(masterPassword, Base64.DEFAULT).equals(decryptedMasterPassword);

                if (this.isAuthenticate) {
                    //save the master password for reuse it in the app
                    this.mutableMasterPassword.postValue(masterPassword);
                }
                return this.isAuthenticate;

            } catch (BadPaddingException e) {
                //the user password is wrong
                return false;
            }
        };
        return ExecutorServiceUtils.executeCallable(executorService, callable);
    }
}
