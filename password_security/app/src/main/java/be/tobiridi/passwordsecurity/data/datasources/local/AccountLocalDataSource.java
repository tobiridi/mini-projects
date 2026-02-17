package be.tobiridi.passwordsecurity.data.datasources.local;

import androidx.lifecycle.LiveData;

import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.util.List;

import be.tobiridi.passwordsecurity.data.database.AppDatabase;
import be.tobiridi.passwordsecurity.data.database.Dao.AccountDao;
import be.tobiridi.passwordsecurity.data.entities.Account;

/**
 * The local data source class for {@link Account} entity.
 */
public final class AccountLocalDataSource implements LocalDataSource {
    private final AccountDao accountDao;

    public AccountLocalDataSource(AppDatabase appDatabase) {
        this.accountDao = appDatabase.getAccountDao();
    }

    public LiveData<List<Account>> getAllAccounts() {
        return this.accountDao.getAllAccounts();
    }

    /**
     * Save the new accounts.
     * @param encryptionKey The encryption key used for encrypt accounts.
     * @param accounts All accounts should be saved.
     * @return An array of rowId of each account saved.
     */
    public long[] saveAccounts(byte[] encryptionKey, Account... accounts) {
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
    }

    /**
     * Update the existing accounts.
     * @param encryptionKey The encryption key used for encrypt accounts.
     * @param accounts An array of updated {@link Account}.
     * @return The number of row updated.
     */
    public int updateAccount(byte[] encryptionKey, Account... accounts) {
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
    }

    /**
     * Delete an account.
     * @param account The account to remove.
     * @return The number of row deleted.
     */
    public int deleteAccount(Account account) {
        return this.accountDao.deleteAccount(account);
    }

    /**
     * Delete all {@link Account} from data source.
     * @return The number of row deleted.
     */
    public int deleteAllAccounts() {
        return this.accountDao.deleteAllAccounts();
    }
}
