package be.tobiridi.passwordsecurity.ui.home;

import static androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.viewmodel.ViewModelInitializer;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.crypto.BadPaddingException;

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
    private Observer<List<Account>> obSourceAccounts;
    private MutableLiveData<List<Account>> mutableAccounts;
    private List<Account> currentAccounts;

    public HomeViewModel(Context context) {
        this._accountDataSource = AccountDataSource.getInstance(context);
        this.sourceAccountsLiveData = this._accountDataSource.getAllAccounts();
        this.currentAccounts = new ArrayList<>();
        this.mutableAccounts = new MutableLiveData<>();

        this.initObservers();
        this.sourceAccountsLiveData.observeForever(this.obSourceAccounts);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        this.sourceAccountsLiveData.removeObserver(this.obSourceAccounts);
    }

    private void initObservers() {
        this.obSourceAccounts = new Observer<List<Account>>() {
            @Override
            public void onChanged(List<Account> accounts) {
                //DB accounts changed

                //add to current accounts
                if (currentAccounts.isEmpty() || currentAccounts.size() < accounts.size()) {
                    accounts.forEach(HomeViewModel.this::decryptAccount);
                    mutableAccounts.setValue(currentAccounts);
                }
            }
        };
    }

    private void decryptAccount(Account newAccount) {
        //decrypt only new account
        if (currentAccounts.stream().noneMatch(a -> a.getId() == newAccount.getId())) {
            try {
                byte[] masterPassword = UserPreferencesDataSource.getAuthenticatedMasterPassword();
                String decryptedCompactData = AESManager.decryptToString(masterPassword, newAccount.getCompactAccount());

                newAccount.setState(Account.EncryptionState.DECRYPTED);
                newAccount.unPackAccountData(decryptedCompactData);

                currentAccounts.add(newAccount);
            } catch (BadPaddingException e) {
                //TODO: manage exception
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Get the current state of accounts.
     * @return The current accounts data.
     */
    public LiveData<List<Account>> getMutableAccounts() {
        return this.mutableAccounts;
    }

    /****************************/
    /* Mutable accounts methods */
    /****************************/

    public boolean deleteAccount(Account deletedAccount) {
        if (this._accountDataSource.deleteAccount(deletedAccount) > 0) {
            //account deleted from DB
            this.currentAccounts.remove(deletedAccount);
            this.mutableAccounts.setValue(this.currentAccounts);
            return true;
        }
        return false;
    }

    /**
     * Filter the accounts by account's name.
     * @param filterAccName The input used to filter the account.
     */
    public void searchFilter(String filterAccName) {
        if (!this.currentAccounts.isEmpty()) {
            List<Account> accFilter = this.currentAccounts.stream()
                    .filter(a -> a.getName().toLowerCase().contains(filterAccName.toLowerCase()))
                    .collect(Collectors.toList());

            this.mutableAccounts.setValue(accFilter);
        }
    }
}