package be.tobiridi.passwordsecurity.data.datasources.local;

import androidx.lifecycle.LiveData;

import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import be.tobiridi.passwordsecurity.data.database.AppDatabase;
import be.tobiridi.passwordsecurity.data.database.Dao.AccountDao;
import be.tobiridi.passwordsecurity.data.entities.Account;
import be.tobiridi.passwordsecurity.data.utils.ExecutorServiceUtils;

/**
 * The local data source class for {@link Account} entity.
 */
public final class AccountLocalDataSource implements LocalDataSource {
    private static ExecutorService executorService;
    private final AccountDao accountDao;

    public AccountLocalDataSource(AppDatabase appDatabase) {
        if(executorService == null || executorService.isShutdown()) {
            executorService = Executors.newSingleThreadExecutor();
        }
        this.accountDao = appDatabase.getAccountDao();
    }

    public LiveData<List<Account>> getAllAccounts() {
        Callable<LiveData<List<Account>>> callable = this.accountDao::getAllAccounts;
        return ExecutorServiceUtils.executeCallable(executorService, callable);
    }

    /**
     * Save the new accounts.
     * @param encryptionKey The encryption key used for encrypt accounts.
     * @param accounts All accounts should be saved.
     * @return An array of rowId of each account saved.
     */
    public long[] saveAccounts(byte[] encryptionKey, Account... accounts) {
        Callable<long[]> callable = () -> {
            for (Account a: accounts) {
                try {
                    a.encrypt(encryptionKey);
                }
                catch (GeneralSecurityException e) {
                    //the encryption key is wrong
                    return new long[0];
                }
            }

            return this.accountDao.insertAccount(accounts);
        };
        return ExecutorServiceUtils.executeCallable(executorService, callable);
    }

    /**
     * Update the existing accounts.
     * @param encryptionKey The encryption key used for encrypt accounts.
     * @param accounts An array of updated {@link Account}.
     * @return The number of row updated.
     */
    public int updateAccount(byte[] encryptionKey, Account... accounts) {
        Callable<Integer> callable = () -> {
            LocalDateTime updateDate = LocalDateTime.now();

            for (Account a: accounts) {
                try {
                    a.setUpdated(updateDate);
                    a.encrypt(encryptionKey);
                }
                catch (GeneralSecurityException e) {
                    //the encryption key is wrong
                    return 0;
                }
            }
            return this.accountDao.updateAccount(accounts);
        };
        return ExecutorServiceUtils.executeCallable(executorService, callable);
    }

    public int deleteAccount(Account account) {
        Callable<Integer> callable = () -> {
            return this.accountDao.deleteAccount(account);
        };
        return ExecutorServiceUtils.executeCallable(executorService, callable);
    }

    public int deleteAllAccounts() {
        Callable<Integer> callable = this.accountDao::deleteAllAccounts;
        return ExecutorServiceUtils.executeCallable(executorService, callable);
    }
}
