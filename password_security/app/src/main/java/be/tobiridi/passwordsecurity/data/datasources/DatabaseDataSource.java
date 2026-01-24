<<<<<<<< HEAD:password_security/app/src/main/java/be/tobiridi/passwordsecurity/datasources/DatabaseDataSource.java
package be.tobiridi.passwordsecurity.datasources;
========
package be.tobiridi.passwordsecurity.data.datasources;
>>>>>>>> 7777d5d ([FIX] reorganize project folders):password_security/app/src/main/java/be/tobiridi/passwordsecurity/data/datasources/DatabaseDataSource.java

import android.content.Context;
import android.database.Cursor;

import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;

<<<<<<<< HEAD:password_security/app/src/main/java/be/tobiridi/passwordsecurity/datasources/DatabaseDataSource.java
import be.tobiridi.passwordsecurity.database.Dao.AccountDao;
import be.tobiridi.passwordsecurity.database.AppDatabase;
import be.tobiridi.passwordsecurity.database.Dao.UserPreferencesDao;
========
import be.tobiridi.passwordsecurity.data.repositories.AccountRepository;
import be.tobiridi.passwordsecurity.data.repositories.UserPreferencesRepository;
import be.tobiridi.passwordsecurity.data.database.Dao.AccountDao;
import be.tobiridi.passwordsecurity.data.database.AppDatabase;
import be.tobiridi.passwordsecurity.data.database.Dao.UserPreferencesDao;
>>>>>>>> 7777d5d ([FIX] reorganize project folders):password_security/app/src/main/java/be/tobiridi/passwordsecurity/data/datasources/DatabaseDataSource.java

/**
 * The data source class to interact with {@link AppDatabase}.
 */
public abstract class DatabaseDataSource {
    private static AppDatabase appDB;
    private static ExecutorService executorService;
    protected AccountDao accountDao;
    protected UserPreferencesDao userPreferencesDao;

    protected DatabaseDataSource(Context ctx) {
        if (appDB == null) {
            appDB = AppDatabase.getInstance(ctx);
            executorService = Executors.newSingleThreadExecutor();
        }
        this.accountDao = appDB.getAccountDao();
        this.userPreferencesDao = appDB.getUserPreferencesDao();
    }

    /**
     * Free all resources used for interact with {@link AppDatabase} and close the database connection.
     * If the resources are already freed, call this method will produce nothing.
     */
    public static void disconnect() {
        if (appDB != null) {
            appDB.close();
            appDB = null;
            executorService.shutdown();

            //TODO: make a better implementation
            //reset all class who extends this class
            AccountRepository.resetInstance();
            UserPreferencesRepository.resetInstance();
        }
    }

    /**
     * Make a checkpoint for SQLite {@code .wal} file and apply all modifications in the database file.
     * </br>
     * Use the {@code PRAGMA wal_checkpoint(TRUNCATE);} MySQL statement.
     */
    public static void makeWalCheckpoint() {
        Cursor cursor = appDB.getOpenHelper().getWritableDatabase().query("PRAGMA wal_checkpoint(TRUNCATE);");
        cursor.moveToNext();
        cursor.close();
    }

    protected void clearAllTables() {
        appDB.clearAllTables();
    }

    /**
     * Execute a {@link Runnable} task to the {@link ExecutorService}.
     * @param command The runnable task.
     * @return {@code true} if the command success {@code false} if an exception occurred.
     */
    protected boolean executeRunnable(Runnable command) {
        try {
            executorService.execute(command);
            return true;
        } catch (NullPointerException | RejectedExecutionException e) {
            return false;
        }
    }

    /**
     * Execute a {@link Callable} task to the {@link ExecutorService}.
     * @param callable The callable task.
     * @return The result of the callable task.
     */
    protected <T> T executeCallable(Callable<T> callable) {
        try {
            return executorService.submit(callable).get();
        } catch (NullPointerException | RejectedExecutionException e) {
            //the callable parameter is null or invalid
            throw new RuntimeException(e);
        } catch (CancellationException | ExecutionException | InterruptedException e) {
            //if the callable task has been cancelled or interrupted
            throw new RuntimeException(e);
        }
    }
}
