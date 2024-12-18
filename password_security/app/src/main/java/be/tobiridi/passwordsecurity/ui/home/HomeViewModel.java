package be.tobiridi.passwordsecurity.ui.home;

import static androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.viewmodel.ViewModelInitializer;

import java.time.chrono.ChronoLocalDate;
import java.util.List;
import java.util.stream.Collectors;

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
    private LiveData<List<Account>> accountsLiveData;

    public HomeViewModel(Context context) {
        this._accountDataSource = new AccountDataSource(context);
        this.accountsLiveData = this.getAllAccountsDao();
    }

    public LiveData<List<Account>> getAccountsLiveData() {
        return this.accountsLiveData;
    }

    public List<Account> accountsSortName() {
        List<Account> acc = this.accountsLiveData.getValue();
        return acc.stream()
                .sorted((a1, a2) -> a1.getName().compareToIgnoreCase(a2.getName()))
                .collect(Collectors.toList());
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        //free resource memory
        this._accountDataSource.closeExecutorService();
    }

    /***************************/
    /* Data Source Dao methods */
    /***************************/

    private LiveData<List<Account>> getAllAccountsDao() {
        return this._accountDataSource.getAllAccounts();
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