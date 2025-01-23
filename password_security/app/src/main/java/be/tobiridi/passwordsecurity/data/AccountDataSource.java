package be.tobiridi.passwordsecurity.data;

import android.content.Context;

import androidx.lifecycle.LiveData;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import be.tobiridi.passwordsecurity.database.AccountDao;
import be.tobiridi.passwordsecurity.database.AppDatabase;
import be.tobiridi.passwordsecurity.security.AESManager;

/**
 * Interact with the Room database.
 * <br/>
 * Can be constructed using one of the getInstance class methods of this class.
 */
public class AccountDataSource {
    private static AccountDataSource INSTANCE;
    private final ExecutorService _service;
    private final AccountDao _accountDao;

    public static AccountDataSource getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new AccountDataSource(context);
        }

        return INSTANCE;
    }

    private AccountDataSource(Context context) {
        this._service = Executors.newSingleThreadExecutor();
        AppDatabase db = AppDatabase.getInstance(context);
        this._accountDao = db.getAccountDao();
    }

    /**
     * Close the Threads and free the resources.
     */
    public void closeExecutorService() {
        this._service.shutdown();
        INSTANCE = null;
    }

    public LiveData<List<Account>> getAllAccounts() {
        Callable<LiveData<List<Account>>> callable = () -> {
            return this._accountDao.getAllAccounts();
        };

        try {
            Future<LiveData<List<Account>>> future = this._service.submit(callable);
            return future.get();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Save the new accounts.
     * @param accounts All accounts should be saved.
     * @return An array of rowId of each account saved.
     */
    public long[] saveAccounts(Account... accounts) {
        Callable<long[]> callable = () -> {
            byte[] masterKey = UserPreferencesDataSource.getAuthenticatedMasterPassword();

            for (Account a: accounts) {
                if (a.getState().equals(Account.EncryptionState.DECRYPTED)) {
                    a.setCompactAccount(AESManager.encryptToStringBase64(masterKey, a.getCompactAccount().getBytes(StandardCharsets.UTF_8)));
                    a.setState(Account.EncryptionState.ENCRYPTED);
                }
            }

            return this._accountDao.insertAccount(accounts);
        };

        try {
            Future<long[]> f = this._service.submit(callable);
            return f.get();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
//
//    public boolean updateAccount(Account account) {
//        //TODO : make verification
//
//        this.accountDao.updateAccount(account);
//        return true;
//    }
//
    public int deleteAccount(Account account) {
        Callable<Integer> callable = () -> {
            return this._accountDao.deleteAccount(account);
        };

        try {
            Future<Integer> f = this._service.submit(callable);
            return f.get();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
