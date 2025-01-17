package be.tobiridi.passwordsecurity.data;

import android.content.Context;
import android.util.Base64;

import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.crypto.BadPaddingException;

import be.tobiridi.passwordsecurity.database.AppDatabase;
import be.tobiridi.passwordsecurity.database.UserPreferencesDao;
import be.tobiridi.passwordsecurity.security.AESManager;
import be.tobiridi.passwordsecurity.security.HashManager;

/**
 * Interact with the Room database.
 * <br/>
 * Can be constructed using one of the getInstance class methods of this class.
 * @see androidx.room.RoomDatabase
 */
public class UserPreferencesDataSource {
    private final ExecutorService _service;
    private final UserPreferencesDao _userPreferencesDao;
    private static UserPreferencesDataSource INSTANCE;
    private static byte[] AUTH_MASTER_PASSWORD;

    public static UserPreferencesDataSource getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new UserPreferencesDataSource(context);
        }

        return INSTANCE;
    }

    private UserPreferencesDataSource(Context context) {
        this._service = Executors.newSingleThreadExecutor();
        AppDatabase db = AppDatabase.getInstance(context);
        this._userPreferencesDao = db.getUserPreferencesDao();
        AUTH_MASTER_PASSWORD = null;
    }

    /**
     * Get the master password to access at the app.
     * @return The master password to encrypt and decrypt data.
     */
    public static byte[] getAuthenticatedMasterPassword() {
        return AUTH_MASTER_PASSWORD;
    }

    /**
     * Close the Threads and free the resources.
     */
    public void closeExecutorService() {
        this._service.shutdown();
        //remove manually the master key from the memory
        Arrays.fill(AUTH_MASTER_PASSWORD, (byte) 0);
        INSTANCE = null;
    }

    /**
     * Attempt to authenticate the user with the provided password.
     * @param userPassword The user password.
     * @return {@code true} If the password matches {@code false} otherwise.
     */
    public boolean authenticateUser(String userPassword) {
        Callable<Boolean> callable = () -> {
            byte[] masterPassword = HashManager.hashStringToBytes(userPassword);
            String encryptedMasterPassword = this._userPreferencesDao.getMasterPassword();

            try {
                String decryptedMasterPassword = AESManager.decryptToStringBase64(masterPassword, encryptedMasterPassword);
                boolean isAuthenticated = Base64.encodeToString(masterPassword, Base64.DEFAULT).equals(decryptedMasterPassword);

                if (isAuthenticated) {
                    //save the master password for reuse it in the app
                    AUTH_MASTER_PASSWORD = masterPassword;
                }

                return isAuthenticated;
            } catch (BadPaddingException e) {
                //the password is wrong
                return false;
            }
        };

        try {
            Future<Boolean> f = this._service.submit(callable);
            return f.get();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Check if the master password of the app exists.
     * @return {@code true} If the master password exists, {@code false} if the master password does not exist.
     */
    public boolean hasMasterPassword() {
        Callable<Boolean> callable = () -> {
            //return null if not found
            return this._userPreferencesDao.getMasterPassword() != null;
        };

        try {
            Future<Boolean> f = this._service.submit(callable);
            return f.get();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Save a new master password to authenticate the user.
     * <br/>
     * It will be replace if the master password already exists.
     * @param newMasterPassword The user master password.
     * @return {@code true} if the master password has been save.
     */
    public boolean saveMasterPassword(String newMasterPassword) {
        Callable<Long> callable = () -> {
            byte[] masterPassword = HashManager.hashStringToBytes(newMasterPassword);
            String encryptedMasterKey = AESManager.encryptToStringBase64(masterPassword, masterPassword);

            //save the master password for reuse it in the app
            AUTH_MASTER_PASSWORD = masterPassword;

            UserPreferences pref = new UserPreferences(encryptedMasterKey);
            return this._userPreferencesDao.saveMasterPassword(pref);
        };

        try {
            Future<Long> f = this._service.submit(callable);
            return f.get() > 0;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * This method will clear all tables present in the database.
     * <br/>
     * <b>Please be careful when you use this method !</b>
     */
    public void destroyAllData(Context context) {
        this._service.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(context);
            db.clearAllTables();
            this.closeExecutorService();
        });
    }
}
