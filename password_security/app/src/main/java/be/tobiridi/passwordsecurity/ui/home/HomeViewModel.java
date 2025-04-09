package be.tobiridi.passwordsecurity.ui.home;

import static androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.viewmodel.ViewModelInitializer;

import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

import be.tobiridi.passwordsecurity.data.Account;
import be.tobiridi.passwordsecurity.data.AccountDataSource;
import be.tobiridi.passwordsecurity.data.UserPreferencesDataSource;
import be.tobiridi.passwordsecurity.security.AESManager;

public class HomeViewModel extends ViewModel {
    /*********************/
    /* ViewModel Factory */
    /*********************/
    public static final ViewModelInitializer<HomeViewModel> initializer = new ViewModelInitializer<>(
            HomeViewModel.class,
            creationExtras -> {
                Application app = creationExtras.get(APPLICATION_KEY);
                assert app != null;

                return new HomeViewModel(app.getApplicationContext());
            }
    );

    private final AccountDataSource _accountDataSource;
    private LiveData<List<Account>> sourceAccountsLiveData;
    private Observer<List<Account>> obDecryptSourceAccounts;
    private MutableLiveData<List<Account>> mutableSourceAccounts;

    public HomeViewModel(Context context) {
        this._accountDataSource = AccountDataSource.getInstance(context);
        this.sourceAccountsLiveData = this._accountDataSource.getAllAccounts();
        this.mutableSourceAccounts = new MutableLiveData<>(new ArrayList<>());
        this.initObservers();
        this.sourceAccountsLiveData.observeForever(this.obDecryptSourceAccounts);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        this.sourceAccountsLiveData.removeObserver(this.obDecryptSourceAccounts);
    }

    private void initObservers() {
        this.obDecryptSourceAccounts = new Observer<List<Account>>() {
            @Override
            public void onChanged(List<Account> dbAccounts) {
                //DB accounts changed, update source accounts list
                //if all DB accounts has been cleared
                if (dbAccounts.isEmpty()) {
                    mutableSourceAccounts.setValue(dbAccounts);
                    return;
                }

                List<Account> mutableAccounts = mutableSourceAccounts.getValue();
                //GET ALL or ADD new accounts created
                if (mutableAccounts.size() < dbAccounts.size()) {
                    dbAccounts.forEach((account) -> {
                        HomeViewModel.this.decryptAccount(account);
                        if (account.getState().equals(Account.EncryptionState.DECRYPTED)) {
                            mutableAccounts.add(account);
                        }
                    });
                }
                //DELETE
                else if (mutableAccounts.size() > dbAccounts.size()) {
                    for (Account deletedAccount: mutableAccounts) {
                        if (!dbAccounts.contains(deletedAccount)) {
                            mutableAccounts.remove(deletedAccount);
                            break;
                        }
                    }
                }
                //UPDATE, lists size are the same
                else {
//                    System.out.println("updated DB accounts");
//                    System.out.println(dbAccounts);
//                    System.out.println(mutableAccounts);
//                    if (mutableAccounts.retainAll(dbAccounts)) {
//                        System.out.println("DB list : " + mutableAccounts);
//                    }
                }
                mutableSourceAccounts.setValue(mutableAccounts);
            }
        };
    }

    private void decryptAccount(Account newAccount) {
        //decrypt only if new account
        var sourceAccounts = mutableSourceAccounts.getValue();
        if (sourceAccounts.stream().noneMatch(a -> a.equals(newAccount))) {
            try {
                byte[] masterPassword = UserPreferencesDataSource.getAuthenticatedMasterPassword();
                String decryptedCompactData = AESManager.decryptToString(masterPassword, newAccount.getCompactAccount());

                newAccount.setState(Account.EncryptionState.DECRYPTED);
                newAccount.unPackAccountData(decryptedCompactData);

            } catch (GeneralSecurityException e) {
                //the master key used is not the same when encryption of the account data
                throw new RuntimeException(e);
            }
        }
    }

    /****************************/
    /* Mutable accounts methods */
    /****************************/

    /**
     * Get the current source accounts.
     * @return The current decrypted source accounts data.
     */
    public LiveData<List<Account>> getMutableSourceAccounts() {
        return this.mutableSourceAccounts;
    }

    public boolean deleteAccount(Account deletedAccount) {
        return this._accountDataSource.deleteAccount(deletedAccount) > 0;
    }

//    public boolean updateAccount(Account account) {
//        return this._accountDataSource.updateAccount(account) > 0;
//    }
}