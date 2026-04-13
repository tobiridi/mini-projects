package be.tobiridi.passwordsecurity.ui.fragments.addAccount;

import static androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.viewmodel.ViewModelInitializer;
import androidx.preference.PreferenceManager;

import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import be.tobiridi.passwordsecurity.R;
import be.tobiridi.passwordsecurity.data.database.AppDatabase;
import be.tobiridi.passwordsecurity.data.datasources.DataSourceProvider;
import be.tobiridi.passwordsecurity.data.datasources.local.AccountLocalDataSource;
import be.tobiridi.passwordsecurity.data.datasources.local.AuthenticationLocalDataSource;
import be.tobiridi.passwordsecurity.data.entities.Account;
import be.tobiridi.passwordsecurity.data.repositories.AccountRepository;
import be.tobiridi.passwordsecurity.data.repositories.UserPreferencesRepository;
import be.tobiridi.passwordsecurity.data.utils.ExecutorServiceUtils;
import be.tobiridi.passwordsecurity.ui.components.accountField.AccountField;

public class AddAccountViewModel extends ViewModel {
    /*********************/
    /* ViewModel Factory */
    /*********************/
    public static final ViewModelInitializer<AddAccountViewModel> initializer = new ViewModelInitializer<>(
            AddAccountViewModel.class,
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

                return new AddAccountViewModel(accRepo, userPrefRepo);
            }
    );

    private final AccountRepository _accountRepository;
    private final UserPreferencesRepository _userPrefRepository;
    private final ExecutorService executorService;
    private final MutableLiveData<AddAccountUiState> mutableAddAccountUiState;

    public AddAccountViewModel(AccountRepository accountRepository, UserPreferencesRepository userPrefRepository) {
        this._accountRepository = accountRepository;
        this._userPrefRepository = userPrefRepository;
        this.executorService = Executors.newSingleThreadExecutor();

        //if TextInputLayout fields are present in the layout, add them to init the EnumSet
        EnumSet<AccountField> defaultFields = EnumSet.of(AccountField.NAME, AccountField.PASSWORD);
        AddAccountUiState uiState = new AddAccountUiState(defaultFields, 0, false, false, null);
        this.mutableAddAccountUiState = new MutableLiveData<>(uiState);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        this.executorService.shutdown();
    }

    public LiveData<AddAccountUiState> getAddAccountUiState() {
        return this.mutableAddAccountUiState;
    }

    public List<AccountField> getRemainingFields() {
        return this.mutableAddAccountUiState.getValue().getRemainingFields();
    }

    public void addAccountField(AccountField accField) {
        AddAccountUiState oldState = this.mutableAddAccountUiState.getValue();
        oldState.getDisplayAccountFields().add(accField);

        AddAccountUiState uiState = new AddAccountUiState(oldState.getDisplayAccountFields(), 0, false, false, null);
        this.mutableAddAccountUiState.setValue(uiState);
    }

    public void removeAccountField(AccountField accField) {
        AddAccountUiState oldState = this.mutableAddAccountUiState.getValue();
        oldState.getDisplayAccountFields().remove(accField);

        AddAccountUiState uiState = new AddAccountUiState(oldState.getDisplayAccountFields(), 0, false, false, null);
        this.mutableAddAccountUiState.setValue(uiState);
    }

    public void resetForm() {
        AddAccountUiState oldState = this.mutableAddAccountUiState.getValue();
        AddAccountUiState uiState = new AddAccountUiState(oldState.getDisplayAccountFields(), 0, false, true, null);
        this.mutableAddAccountUiState.setValue(uiState);
    }

    public void createAccount(Account createAccount) {
        Callable<Boolean> callable = () -> {
            byte[] masterPwd = this._userPrefRepository.getMasterPassword();
            return this._accountRepository.addAccounts(masterPwd, createAccount);
        };
        boolean isSuccess = ExecutorServiceUtils.executeCallable(this.executorService, callable);

        AddAccountUiState oldState = this.mutableAddAccountUiState.getValue();
        int msgId;
        boolean hasErrors;
        if (isSuccess) {
            msgId = R.string.msg_add_account_success;
            hasErrors = false;
        }
        else {
            msgId = R.string.msg_add_account_fail;
            hasErrors = true;
        }

        AddAccountUiState uiState = new AddAccountUiState(oldState.getDisplayAccountFields(), msgId, hasErrors, false, createAccount);
        this.mutableAddAccountUiState.postValue(uiState);
    }
}