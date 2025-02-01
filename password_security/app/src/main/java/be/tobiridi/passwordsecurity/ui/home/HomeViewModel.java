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
    private Observer<List<Account>> obDecryptSourceAccounts;
    private MutableLiveData<List<Account>> mutableSourceAccounts;
    private List<Account> sourceAccounts;

    public HomeViewModel(Context context) {
        this._accountDataSource = AccountDataSource.getInstance(context);
        this.sourceAccountsLiveData = this._accountDataSource.getAllAccounts();
        this.sourceAccounts = new ArrayList<>();
        this.mutableSourceAccounts = new MutableLiveData<>();

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
            public void onChanged(List<Account> accounts) {
                //DB accounts changed, get all or add new account
                if (sourceAccounts.isEmpty() || sourceAccounts.size() < accounts.size()) {
                    accounts.forEach(HomeViewModel.this::decryptAccount);
                    updateMutableSourceAccounts(sourceAccounts);
                }
            }
        };
    }

    private void decryptAccount(Account newAccount) {
        //decrypt only new account
        if (this.sourceAccounts.stream().noneMatch(a -> a.equals(newAccount))) {
            try {
                byte[] masterPassword = UserPreferencesDataSource.getAuthenticatedMasterPassword();
                String decryptedCompactData = AESManager.decryptToString(masterPassword, newAccount.getCompactAccount());

                newAccount.setState(Account.EncryptionState.DECRYPTED);
                newAccount.unPackAccountData(decryptedCompactData);

                this.sourceAccounts.add(newAccount);
            } catch (BadPaddingException e) {
                //TODO: manage exception
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

    public void updateMutableSourceAccounts(List<Account> accounts) {
//        if (this.mutableAccounts.isInitialized()) {
//            List<Account> current = this.editableAccounts.getValue();
//            if (!current.isEmpty()) {
//                current.clear();
//            }
//            current.addAll(accounts);
//        }
        this.mutableSourceAccounts.setValue(accounts);
    }

    public boolean deleteAccount(Account deletedAccount) {
        if (this._accountDataSource.deleteAccount(deletedAccount) > 0) {
            //account deleted from DB
            this.sourceAccounts.remove(deletedAccount);
            this.updateMutableSourceAccounts(this.sourceAccounts);
            return true;
        }
        return false;
    }
}