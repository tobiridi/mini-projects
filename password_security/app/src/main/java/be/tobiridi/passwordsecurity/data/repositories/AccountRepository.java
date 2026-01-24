<<<<<<<< HEAD:password_security/app/src/main/java/be/tobiridi/passwordsecurity/datasources/AccountDataSource.java
package be.tobiridi.passwordsecurity.datasources;
========
package be.tobiridi.passwordsecurity.data.repositories;
>>>>>>>> 7777d5d ([FIX] reorganize project folders):password_security/app/src/main/java/be/tobiridi/passwordsecurity/data/repositories/AccountRepository.java

import android.content.Context;

import androidx.lifecycle.LiveData;

import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Callable;

<<<<<<<< HEAD:password_security/app/src/main/java/be/tobiridi/passwordsecurity/datasources/AccountDataSource.java
import be.tobiridi.passwordsecurity.entities.Account;
========
import be.tobiridi.passwordsecurity.data.datasources.DatabaseDataSource;
import be.tobiridi.passwordsecurity.data.entities.Account;
>>>>>>>> 7777d5d ([FIX] reorganize project folders):password_security/app/src/main/java/be/tobiridi/passwordsecurity/data/repositories/AccountRepository.java

/**
 * Can be constructed using one of the getInstance class methods of this class.
 */
public class AccountRepository extends DatabaseDataSource {
    private static AccountRepository INSTANCE;

    public static AccountRepository getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new AccountRepository(context);
        }
        return INSTANCE;
    }

    private AccountRepository(Context context) {
        super(context);
    }

    // TODO: 31/12/2025 do a better implementation
    public static void resetInstance() {
        INSTANCE = null;
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
            byte[] masterKey = UserPreferencesRepository.getAuthenticatedMasterPassword();

            for (Account a: accounts) {
                try {
                    a.encrypt(masterKey);
                }
                catch (GeneralSecurityException e) {
                    //the master key is wrong
                    return new long[0];
                }
            }

            return this.accountDao.insertAccount(accounts);
        };
        return this.executeCallable(callable);
    }

    /**
     * Update the existing accounts.
     * @param accounts An array of updated {@link Account}.
     * @return The number of row updated.
     */
    public int updateAccount(Account... accounts) {
        Callable<Integer> callable = () -> {
            byte[] masterKey = UserPreferencesRepository.getAuthenticatedMasterPassword();
            LocalDateTime updateDate = LocalDateTime.now();

            for (Account a: accounts) {
                try {
                    a.encrypt(masterKey);
                    a.setUpdated(updateDate);
                }
                catch (GeneralSecurityException e) {
                    //the master key is wrong
                    return 0;
                }
            }
            return this.accountDao.updateAccount(accounts);
        };
        return this.executeCallable(callable);
    }

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
