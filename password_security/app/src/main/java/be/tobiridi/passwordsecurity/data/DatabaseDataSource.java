package be.tobiridi.passwordsecurity.data;

import android.content.Context;

import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;

import be.tobiridi.passwordsecurity.database.AccountDao;
import be.tobiridi.passwordsecurity.database.AppDatabase;
import be.tobiridi.passwordsecurity.database.UserPreferencesDao;

/**
 * The common class to interact with {@link AppDatabase}.
 * <br/>
 * Every class should be extends this class if needed to interact with the {@link androidx.room.RoomDatabase}.
 * @author Tony
 */
public class DatabaseDataSource {
    private static DatabaseDataSource INSTANCE;
    private ExecutorService executorService;
    protected AccountDao accountDao;
    protected UserPreferencesDao userPreferencesDao;

    protected DatabaseDataSource(Context ctx) {
        //access via getInstance method, executed only once
        if (INSTANCE == null) {
            AppDatabase db = AppDatabase.getInstance(ctx);
            this.executorService = Executors.newSingleThreadExecutor();
            this.accountDao = db.getAccountDao();
            this.userPreferencesDao = db.getUserPreferencesDao();
        }
        else {
            //retrieve from INSTANCE for the children class
            this.executorService = INSTANCE.executorService;
            this.accountDao = INSTANCE.accountDao;
            this.userPreferencesDao = INSTANCE.userPreferencesDao;
        }
    }

    protected static DatabaseDataSource getInstance(Context ctx) {
        if (INSTANCE == null) {
            INSTANCE = new DatabaseDataSource(ctx);
        }
        return INSTANCE;
    }

    /**
     * Execute a {@link Runnable} task to the {@link ExecutorService}.
     * @param command The runnable task.
     * @return {@code true} if the command success {@code false} if an exception occurred.
     */
    protected boolean executeRunnable(Runnable command) {
        try {
            this.executorService.execute(command);
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
            return this.executorService.submit(callable).get();
        } catch (NullPointerException | RejectedExecutionException e) {
            //the callable parameter is null or invalid
            throw new RuntimeException(e);
        } catch (CancellationException | ExecutionException | InterruptedException e) {
            //if the callable task has been cancelled or interrupted
            throw new RuntimeException(e);
        }
    }
}
