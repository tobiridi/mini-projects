package be.tobiridi.passwordsecurity.data.repositories;

import androidx.lifecycle.LiveData;

import java.util.List;

import be.tobiridi.passwordsecurity.data.datasources.local.AccountLocalDataSource;
import be.tobiridi.passwordsecurity.data.entities.Account;

/**
 * Centralize for the ui layer how {@link Account} entity can be manipulate.
 */
public class AccountRepository {
    private static AccountLocalDataSource accountDataSource;

    public AccountRepository (AccountLocalDataSource dataSource) {
        if(accountDataSource == null) {
            accountDataSource = dataSource;
        }
    }

    public LiveData<List<Account>> getLiveAllAccounts() {
        return accountDataSource.getLiveAllAccounts();
    }

    public List<Account> getAllAccounts() {
        return accountDataSource.getAllAccounts();
    }

    public boolean addAccounts(byte[] encryptionKey, Account... accounts) {
        //ensure valid account
        for (Account acc: accounts) {
            if (acc.isEncrypted())
                return false;
            if (acc.getName().trim().isEmpty() || acc.getPassword().trim().isEmpty())
                return false;
        }

        return accountDataSource.saveAccounts(encryptionKey, accounts).length > 0 ;
    }

    public boolean deleteAccount(Account account) {
        if(account.getId() < 1)
            return false;

        return accountDataSource.deleteAccount(account) > 0;
    }

    public boolean updateAccounts(byte[] encryptionKey, Account... accounts) {
        //ensure valid account
        for (Account acc: accounts) {
            if (acc.getId() < 1 || acc.isEncrypted())
                return false;
            if (acc.getName().trim().isEmpty() || acc.getPassword().trim().isEmpty())
                return false;
        }

        return accountDataSource.updateAccount(encryptionKey, accounts) > 0;
    }

    public boolean deleteAllAccounts() {
        return accountDataSource.deleteAllAccounts() > 0;
    }

}
