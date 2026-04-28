package be.tobiridi.passwordsecurity.ui.fragments.settings;

import static androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY;

import android.app.Application;
import android.content.ContentResolver;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.FileUtils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.viewmodel.ViewModelInitializer;
import androidx.preference.PreferenceManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import be.tobiridi.passwordsecurity.data.database.AppDatabase;
import be.tobiridi.passwordsecurity.data.datasources.DataSourceProvider;
import be.tobiridi.passwordsecurity.data.datasources.local.AccountLocalDataSource;
import be.tobiridi.passwordsecurity.data.datasources.local.AuthenticationLocalDataSource;
import be.tobiridi.passwordsecurity.data.entities.Account;
import be.tobiridi.passwordsecurity.data.repositories.AccountRepository;
import be.tobiridi.passwordsecurity.data.repositories.UserPreferencesRepository;
import be.tobiridi.passwordsecurity.data.utils.ExecutorServiceUtils;

public class SettingsViewModel extends ViewModel {
    /*********************/
    /* ViewModel Factory */
    /*********************/
    public static final ViewModelInitializer<SettingsViewModel> initializer = new ViewModelInitializer<>(
            SettingsViewModel.class,
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

                return new SettingsViewModel(userPrefRepo, accRepo, PreferenceManager.getDefaultSharedPreferences(app));
            }
    );

    /** 2018 - new SQLite MIME type */
    public final String SQLITE_MIME_TYPE = "application/vnd.sqlite3";
    public final String[] OPEN_DOCUMENT_MIME_TYPE = {SQLITE_MIME_TYPE, "application/octet-stream"};
    private SharedPreferences preferences;
    private final UserPreferencesRepository _userPrefRepository;
    private final AccountRepository _accountRepository;
    private final ExecutorService executorService;
    private final MutableLiveData<SettingsUiState> mutableSettingsUiState;

    public SettingsViewModel(UserPreferencesRepository userPrefRepository, AccountRepository accountRepository, SharedPreferences preferences) {
        this.executorService = Executors.newSingleThreadExecutor();
        this._userPrefRepository = userPrefRepository;
        this._accountRepository = accountRepository;
        this.preferences = preferences;

        boolean enAuto = this.preferences.getBoolean(SettingsPreferenceKey.EN_AUTOMATION, false);
        boolean enNotif = this.preferences.getBoolean(SettingsPreferenceKey.EN_NOTIF, false);
        SettingsUiState uiState = new SettingsUiState(enAuto, enNotif);
        this.mutableSettingsUiState = new MutableLiveData<>(uiState);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        this.executorService.shutdown();
    }

    public LiveData<SettingsUiState> getSettingsUiState() {
        return this.mutableSettingsUiState;
    }

    public boolean createBackup(File dbFile, ContentResolver resolver, Uri fileCreated) {
        Callable<Boolean> exportTask = (() -> {
            AppDatabase.makeWalCheckpoint();

            try (FileInputStream fins = new FileInputStream(dbFile);
                 FileOutputStream fos = (FileOutputStream) resolver.openOutputStream(fileCreated)) {

                long bytesCopied = FileUtils.copy(fins, fos);
                fos.flush();
                return bytesCopied > 0;

            } catch (IOException e) {
                return false;
            }
        });

        return ExecutorServiceUtils.executeCallable(executorService, exportTask);
    }

    public boolean importBackup(File dbFile, ContentResolver resolver, Uri fileSelected) {
        Callable<Boolean> importTask = (() -> {
            AppDatabase.closeDatabase();
            DataSourceProvider provider = DataSourceProvider.getProvider();
            provider.clearAllDataSources();

            try (FileInputStream fins = (FileInputStream) resolver.openInputStream(fileSelected);
                 FileOutputStream fos = new FileOutputStream(dbFile)) {

                long bytesCopied = FileUtils.copy(fins, fos);
                fos.flush();
                return bytesCopied > 0;

            } catch (IOException e) {
                return false;
            }
        });

        return ExecutorServiceUtils.executeCallable(executorService, importTask);
    }

    public boolean defineNewMasterPassword(String masterPassword) {
        Callable<Boolean> callable = () -> {
            //re encrypt all accounts with new master password
            Account[] decryptedAccounts = this._accountRepository.getAllAccounts()
                    .stream()
                    .peek(account -> {
                        try {
                            account.decrypt(this._userPrefRepository.getMasterPassword());
                        } catch (GeneralSecurityException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .toArray(Account[]::new);

            this._userPrefRepository.saveMasterPassword(masterPassword);
            return this._accountRepository.updateAccounts(this._userPrefRepository.getMasterPassword(), decryptedAccounts);
        };

        return ExecutorServiceUtils.executeCallable(this.executorService, callable);
    }

    public boolean deleteAllAccounts() {
        return ExecutorServiceUtils.executeCallable(this.executorService, this._accountRepository::deleteAllAccounts);
    }

    public String generateBackupFileName() {
        String n = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
        return "backup-" + n + ".sqlite";
    }

    public void updateAutomationPref(boolean value) {
        SettingsUiState oldState = this.mutableSettingsUiState.getValue();
        SettingsUiState uiState = new SettingsUiState(value, oldState.isNotificationActive);
        this.mutableSettingsUiState.setValue(uiState);
    }

    public void updateNotifPref(boolean value) {
        SettingsUiState oldState = this.mutableSettingsUiState.getValue();
        SettingsUiState uiState = new SettingsUiState(oldState.isAutomationActive, value);
        this.mutableSettingsUiState.setValue(uiState);
    }
}