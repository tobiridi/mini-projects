package be.tobiridi.passwordsecurity.ui.home;

import static androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.viewmodel.ViewModelInitializer;

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
    private LiveData<List<Account>> sourceAccounts;
    private MutableLiveData<List<Account>> mutableAccounts;
    private Observer<List<Account>> obDecryptAccounts;

    public HomeViewModel(Context context) {
        this._accountDataSource = AccountDataSource.getInstance(context);
        this.sourceAccounts = this._accountDataSource.getAllAccounts();
        this.mutableAccounts = new MutableLiveData<>();

        this.obDecryptAccounts = new Observer<List<Account>>() {
            @Override
            public void onChanged(List<Account> accounts) {
                //TODO: manage exception
                //TODO: optimise
                byte[] masterKey = UserPreferencesDataSource.getAuthenticatedMasterPassword();
                for (Account a: accounts) {
                    try {
                        a.setName(AESManager.decryptToString(masterKey, a.getName()));
                        a.setEmail(AESManager.decryptToString(masterKey, a.getEmail()));
                        a.setPassword(AESManager.decryptToString(masterKey, a.getPassword()));
                    } catch (BadPaddingException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        };
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        //TODO: maybe better practice
        this.sourceAccounts.removeObserver(this.obDecryptAccounts);
    }

    public void updateMutableAccounts(List<Account> newList) {
        this.mutableAccounts.setValue(newList);
    }

    /**
     * Get the current accounts data from {@link AccountDataSource}.
     * @return The current accounts data.
     */
    public LiveData<List<Account>> getSourceAccounts() {
        //TODO: maybe better practice
        this.sourceAccounts.observeForever(this.obDecryptAccounts);
        return this.sourceAccounts;
    }

    /**
     * Get the accounts modified by the user.
     * @return The current modified accounts data.
     */
    public LiveData<List<Account>> getMutableAccounts() {
        return this.mutableAccounts;
    }

    /**
     * Retrieve the current list of accounts present in the {@link HomeViewModel#getSourceAccounts()}
     * @return The list of accounts.
     */
    private List<Account> getAccounts() {
        return this.sourceAccounts.getValue();
    }

    /****************************/
    /* Mutable accounts methods */
    /****************************/

    /**
     * Filter the accounts by account's name.
     * @param filterAccName The input used to filter the account.
     */
    public void searchFilter(String filterAccName) {
        if (this.sourceAccounts.isInitialized()) {
            List<Account> accFilter = this.getAccounts().stream()
                    .filter(a -> a.getName().toLowerCase().contains(filterAccName.toLowerCase()))
                    .collect(Collectors.toList());

            this.mutableAccounts.setValue(accFilter);
        }
    }
}