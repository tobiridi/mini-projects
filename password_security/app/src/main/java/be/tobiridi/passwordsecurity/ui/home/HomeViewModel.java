package be.tobiridi.passwordsecurity.ui.home;

import static androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.viewmodel.ViewModelInitializer;

import java.util.List;

import be.tobiridi.passwordsecurity.data.Account;
import be.tobiridi.passwordsecurity.data.AccountDataSource;

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
    private final MutableLiveData<List<Account>> _mutableAccounts;

    public HomeViewModel(Context context) {
        this._accountDataSource = new AccountDataSource(context);
        this._mutableAccounts = new MutableLiveData<>(this.getAllAccountsDao());
    }

    public LiveData<List<Account>> getAllAccounts() {
        return this._mutableAccounts;
    }

    /***************************/
    /* Data Source Dao methods */
    /***************************/

    private List<Account> getAllAccountsDao() {
        // change to return "LiveData<List<Account>>" if better
        return this._accountDataSource.getAllAccounts().getValue();
    }

//    private boolean deleteAccountDao(Account account) {
//        return this._accountDataSource.deleteAccount(account);
//    }
//
//    private boolean saveAccountDao(Account... accounts) {
//        return this._accountDataSource.saveAccount(accounts);
//    }
//
//    private boolean updateAccountDao(Account account) {
//        return this._accountDataSource.updateAccount(account);
//    }

}