package be.tobiridi.passwordsecurity.data;

import android.content.Context;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import be.tobiridi.passwordsecurity.database.AppDatabase;

/**
 * Interact with the Room database.
 */
public class AccountDataSource {
    private final ExecutorService _service;
    private final Context _context;

    public AccountDataSource(Context context) {
        this._service = Executors.newSingleThreadExecutor();
        this._context = context;
    }

    public LiveData<List<Account>> getAllAccounts() {
        LiveData<List<Account>> accounts;

        Callable<LiveData<List<Account>>> callable = () -> {
            AppDatabase db = AppDatabase.getInstance(this._context);
            return db.getAccountDao().getAllAccounts();
        };

        try {
            Future<LiveData<List<Account>>> future = this._service.submit(callable);
            accounts = future.get();
            this._service.shutdown();
            return accounts;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

//    public boolean saveAccount(Account... accounts) {
//         //TODO : make verification
////        for (Account acc: accounts) {
////        }
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
