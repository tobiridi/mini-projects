package be.tobiridi.passwordsecurity.data;

import android.content.Context;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import be.tobiridi.passwordsecurity.database.AccountDao;
import be.tobiridi.passwordsecurity.database.AppDatabase;

/**
 * Interact with the Room database.
 */
public class AccountDataSource {
    private final ExecutorService _service;
    private final AccountDao _accountDao;

    public AccountDataSource(Context context) {
        this._service = Executors.newSingleThreadExecutor();
        AppDatabase db = AppDatabase.getInstance(context);
        this._accountDao = db.getAccountDao();
    }

    public void closeExecutorService() {
        this._service.shutdown();
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

//    public boolean saveAccount(Account... accounts) {
//         //TODO : make verification
////        for (Account acc: accounts) {
////        }
//        this.service.execute(() -> this.accountDao.insertAccount(accounts));
//
//        this.accountDao.insertAccount(accounts);
//        return true;
//    }
//
//    public boolean updateAccount(Account account) {
//        //TODO : make verification
//
//        this.accountDao.updateAccount(account);
//        return true;
//    }
//
//    public boolean deleteAccount(Account account) {
//        //TODO : make verification
//
//        this.accountDao.deleteAccount(account);
//        return true;
//    }

}
