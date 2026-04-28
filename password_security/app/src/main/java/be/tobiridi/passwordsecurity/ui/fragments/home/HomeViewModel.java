package be.tobiridi.passwordsecurity.ui.fragments.home;

import static androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.viewmodel.ViewModelInitializer;
import androidx.preference.PreferenceManager;

import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import be.tobiridi.passwordsecurity.data.database.AppDatabase;
import be.tobiridi.passwordsecurity.data.datasources.DataSourceProvider;
import be.tobiridi.passwordsecurity.data.datasources.local.AccountLocalDataSource;
import be.tobiridi.passwordsecurity.data.datasources.local.AuthenticationLocalDataSource;
import be.tobiridi.passwordsecurity.data.entities.Account;
import be.tobiridi.passwordsecurity.data.repositories.AccountRepository;
import be.tobiridi.passwordsecurity.data.repositories.UserPreferencesRepository;
import be.tobiridi.passwordsecurity.data.utils.ExecutorServiceUtils;

public class HomeViewModel extends ViewModel {
    /*********************/
    /* ViewModel Factory */
    /*********************/
    public static final ViewModelInitializer<HomeViewModel> initializer = new ViewModelInitializer<>(
            HomeViewModel.class,
            creationExtras -> {
                Application app = creationExtras.get(APPLICATION_KEY);
                assert app != null;

                DataSourceProvider provider = DataSourceProvider.getProvider();
                AccountLocalDataSource accDataSource = provider.getLocalDataSource(AccountLocalDataSource.class);
                AuthenticationLocalDataSource authDataSource = provider.getLocalDataSource(AuthenticationLocalDataSource.class);
                if (accDataSource == null) {
                    AppDatabase db = AppDatabase.getInstance(app);
                    accDataSource = new AccountLocalDataSource(db);
                    provider.addDataSource(accDataSource);
                }
                if (authDataSource == null) {
                    AppDatabase db = AppDatabase.getInstance(app);
                    authDataSource = new AuthenticationLocalDataSource(PreferenceManager.getDefaultSharedPreferences(app), db);
                    provider.addDataSource(authDataSource);
                }

                AccountRepository accRepo = new AccountRepository(accDataSource);
                UserPreferencesRepository userPrefRepo = new UserPreferencesRepository(authDataSource);

                return new HomeViewModel(accRepo, userPrefRepo);
            }
    );

    private final AccountRepository _accountRepository;
    private final UserPreferencesRepository _userPrefRepository;
    private final ExecutorService executorService;
    private final LiveData<List<Account>> encryptedSourceAccounts;
    private Observer<List<Account>> obDecryptSourceAccounts;
    private final MutableLiveData<HomeUiState> mutableHomeUiState;

    public HomeViewModel(AccountRepository accountRepository, UserPreferencesRepository userPrefRepository) {
        this._accountRepository = accountRepository;
        this._userPrefRepository = userPrefRepository;
        this.executorService = Executors.newSingleThreadExecutor();

        HomeUiState uiState = new HomeUiState(new ArrayList<>(), true, null, false);
        this.mutableHomeUiState = new MutableLiveData<>(uiState);
        this.encryptedSourceAccounts = ExecutorServiceUtils.executeCallable(this.executorService, this._accountRepository::getLiveAllAccounts);

        this.initObservers();
        this.encryptedSourceAccounts.observeForever(this.obDecryptSourceAccounts);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        this.executorService.shutdown();
        this.encryptedSourceAccounts.removeObserver(this.obDecryptSourceAccounts);
    }

    public LiveData<HomeUiState> getHomeUiState() {
        return this.mutableHomeUiState;
    }

    private void initObservers() {
        this.obDecryptSourceAccounts = (List<Account> dbAccounts) -> {
            //DB accounts changed, update source accounts list
            HomeUiState oldUiState = this.mutableHomeUiState.getValue();

            //if all DB accounts has been cleared
            if (dbAccounts.isEmpty()) {
                HomeUiState newUiState = new HomeUiState(dbAccounts, false, oldUiState.getSearchText(), oldUiState.isFiltering());
                this.mutableHomeUiState.setValue(newUiState);
                return;
            }

            // TODO: 16/02/2026 verify if all works properly
            //execute all others tasks on ExecutorService thread
            Runnable runnable = () -> {
                List<Account> displayedAccounts = oldUiState.getDecryptedAccounts();

                //GET ALL or ADD new accounts created
                if (oldUiState.accountsCount() < dbAccounts.size()) {
                    byte[] masterPassword = this._userPrefRepository.getMasterPassword();
                    dbAccounts.forEach((account) -> {
                        HomeViewModel.this.decryptAccount(account, masterPassword);
                        if (!account.isEncrypted()) {
                            displayedAccounts.add(account);
                        }
                    });
                }
                //DELETE
                else if (oldUiState.accountsCount() > dbAccounts.size()) {
                    displayedAccounts.removeIf(displayAcc -> !dbAccounts.contains(displayAcc));
                }
                //UPDATE, lists size are the same
                else {
                    int index = 0;
                    byte[] masterPassword = this._userPrefRepository.getMasterPassword();

                    for (Account updatedAccount: displayedAccounts) {
                        Account dbAcc = dbAccounts.get(index);
                        if (dbAcc.getUpdated().isAfter(updatedAccount.getUpdated())) {
                            //change the id to indicate a different object
                            updatedAccount.setId(-1);
                            HomeViewModel.this.decryptAccount(dbAcc, masterPassword);
                            if (!dbAcc.isEncrypted()) {
                                displayedAccounts.set(index, dbAcc);
                            }
                        }
                        index++;
                    }
                }

                HomeUiState newUiState = new HomeUiState(displayedAccounts, false, oldUiState.getSearchText(), oldUiState.isFiltering());
                this.mutableHomeUiState.postValue(newUiState);
            };

            //update the state the time of runnable task is done
            HomeUiState newUiState = new HomeUiState(oldUiState.getDecryptedAccounts(), true, oldUiState.getSearchText(), oldUiState.isFiltering());
            this.mutableHomeUiState.postValue(newUiState);

            ExecutorServiceUtils.executeRunnable(this.executorService, runnable);
        };
    }

    private void decryptAccount(Account newAccount, byte[] masterPassword) {
        //decrypt only if new account
        var uiStateAccounts = this.mutableHomeUiState.getValue().getDecryptedAccounts();
        if (uiStateAccounts.stream().noneMatch(a -> a.equals(newAccount))) {
            try {
                newAccount.decrypt(masterPassword);

            } catch (GeneralSecurityException e) {
                //the master key used is not the same when encryption of the account data
                throw new RuntimeException(e);
            }
        }
    }

    public void updateSearchText(String searchText) {
        HomeUiState oldState = this.mutableHomeUiState.getValue();
        HomeUiState uiState = new HomeUiState(oldState.getDecryptedAccounts(), false, searchText, true);
        this.mutableHomeUiState.setValue(uiState);
    }

    public boolean deleteAccount(Account deletedAccount) {
        return ExecutorServiceUtils.executeCallable(this.executorService, () -> this._accountRepository.deleteAccount(deletedAccount));
    }
}