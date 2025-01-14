package be.tobiridi.passwordsecurity.data;

import android.content.Context;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import be.tobiridi.passwordsecurity.database.AppDatabase;
import be.tobiridi.passwordsecurity.database.UserPreferencesDao;

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
    }

    /**
     * Close the Threads and free the resources.
     */
    public void closeExecutorService() {
        this._service.shutdown();
        INSTANCE = null;
    }

    /**
     * Attempt to authenticate the user with the provided password.
     * @param base64HashPassword The hashed user password in Base64 format.
     * @return {@code true} If the password matches {@code false} otherwise.
     * @see be.tobiridi.passwordsecurity.security.HashManager
     */
    public boolean authenticateUser(String base64HashPassword) {
        Callable<Boolean> callable = () -> {
            return this._userPreferencesDao.getMasterPassword().equals(base64HashPassword);
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
     * Replace it if the master password already exists.
     * @param base64HashPassword The hashed user password in Base64 format.
     * @return {@code true} if the master password has been save.
     * @see be.tobiridi.passwordsecurity.security.HashManager
     */
    public boolean saveMasterPassword(String base64HashPassword) {
        Callable<Long> callable = () -> {
            UserPreferences pref = new UserPreferences(base64HashPassword);
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
