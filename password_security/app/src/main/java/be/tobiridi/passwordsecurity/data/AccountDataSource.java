package be.tobiridi.passwordsecurity.data;

import android.content.Context;

import androidx.lifecycle.LiveData;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Callable;

import be.tobiridi.passwordsecurity.security.AESManager;

/**
 * Can be constructed using one of the getInstance class methods of this class.
 */
public class AccountDataSource extends DatabaseDataSource {
    private static AccountDataSource INSTANCE;

    public static AccountDataSource getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new AccountDataSource(context);
        }
        return INSTANCE;
    }

    private AccountDataSource(Context context) {
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

    /**
     * Update the existing accounts.
     * @param accounts An array of updated {@link Account}.
     * @return The number of row updated.
     * @throws IllegalArgumentException If at least one account has {@link be.tobiridi.passwordsecurity.data.Account.EncryptionState#ENCRYPTED} state,
     * prevent to encrypt and store wrong compact data.
     */
    public int updateAccount(Account... accounts) throws IllegalArgumentException {
        Callable<Integer> callable = () -> {
            byte[] masterKey = UserPreferencesDataSource.getAuthenticatedMasterPassword();
            LocalDateTime updateDate = LocalDateTime.now();

            for (Account a: accounts) {
                if (a.getState().equals(Account.EncryptionState.DECRYPTED)) {
                    a.setCompactAccount(AESManager.encryptToStringBase64(masterKey, a.getCompactAccount().getBytes(StandardCharsets.UTF_8)));
                    a.setState(Account.EncryptionState.ENCRYPTED);
                    a.setUpdated(updateDate);
                }
                else {
                    //normally only happened in development, encrypt wrong compact data
                    throw new IllegalArgumentException("At least one provided accounts has encrypted state");
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
