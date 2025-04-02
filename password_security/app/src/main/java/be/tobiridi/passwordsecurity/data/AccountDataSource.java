package be.tobiridi.passwordsecurity.data;

import android.content.Context;

import androidx.lifecycle.LiveData;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.Callable;

import be.tobiridi.passwordsecurity.security.AESManager;

/**
 * Can be constructed using one of the getInstance class methods of this class.
 */
public class AccountDataSource extends DatabaseDataSource {
    private static AccountDataSource INSTANCE;

    public static AccountDataSource getInstance(Context context) {
        DatabaseDataSource.getInstance(context);
        if (INSTANCE == null) {
            INSTANCE = new AccountDataSource(context);
        }
        return INSTANCE;
    }

    private AccountDataSource(Context context) {
        super(context);
    }

    public LiveData<List<Account>> getAllAccounts() {
        Callable<LiveData<List<Account>>> callable = () -> {
            return this.accountDao.getAllAccounts();
        };
        return this.executeCallable(callable);
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

            return this.accountDao.insertAccount(accounts);
        };
        return this.executeCallable(callable);
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
            return this.accountDao.deleteAccount(account);
        };
        return this.executeCallable(callable);
    }

    public int deleteAllAccounts() {
        Callable<Integer> callable = () -> {
            return this.accountDao.deleteAllAccounts();
        };
        return this.executeCallable(callable);
    }
}
