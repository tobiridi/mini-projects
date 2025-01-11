package be.tobiridi.passwordsecurity.data;

import android.content.Context;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import be.tobiridi.passwordsecurity.database.AppDatabase;
import be.tobiridi.passwordsecurity.database.UserPreferencesDao;
import be.tobiridi.passwordsecurity.security.HashManager;

/**
 * Interact with the Room database.
 * <br/>
 * Can be constructed using one of the getInstance class methods of this class.
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
     * @return True if the password matches false otherwise.
     */
    public boolean authenticateUser(String base64HashPassword) {
        Callable<Boolean> callable = () -> {
            String dbData = this._userPreferencesDao.getMasterPassword();

            //testing, ok
            System.out.println("DB password : " + dbData);
            if (dbData == null) {
                dbData = HashManager.hashStringToStringBase64("tony");
            }
            System.out.println("DB password : " + dbData);
            return dbData.equals(base64HashPassword);

            //return this._userPreferencesDao.getMasterPassword().equals(base64HashPassword);
        };

        try {
            Future<Boolean> f = this._service.submit(callable);
            return f.get();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
