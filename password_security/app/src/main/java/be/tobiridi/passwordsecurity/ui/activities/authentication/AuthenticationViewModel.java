package be.tobiridi.passwordsecurity.ui.activities.authentication;

import static androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.viewmodel.ViewModelInitializer;
import androidx.preference.PreferenceManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import be.tobiridi.passwordsecurity.R;
import be.tobiridi.passwordsecurity.data.database.AppDatabase;
import be.tobiridi.passwordsecurity.data.datasources.DataSourceProvider;
import be.tobiridi.passwordsecurity.data.datasources.local.AuthenticationLocalDataSource;
import be.tobiridi.passwordsecurity.data.repositories.AuthenticationRepository;
import be.tobiridi.passwordsecurity.data.repositories.UserPreferencesRepository;
import be.tobiridi.passwordsecurity.data.utils.ExecutorServiceUtils;

public class AuthenticationViewModel extends ViewModel {
    /*********************/
    /* ViewModel Factory */
    /*********************/
    public static final ViewModelInitializer<AuthenticationViewModel> initializer = new ViewModelInitializer<>(
            AuthenticationViewModel.class,
            creationExtras -> {
                Application app = creationExtras.get(APPLICATION_KEY);
                assert app != null;

                DataSourceProvider provider = DataSourceProvider.getProvider();
                AuthenticationLocalDataSource authDataSource = provider.getLocalDataSource(AuthenticationLocalDataSource.class);
                if (authDataSource == null) {
                    AppDatabase db = AppDatabase.getInstance(app);
                    authDataSource = new AuthenticationLocalDataSource(PreferenceManager.getDefaultSharedPreferences(app), db);
                    provider.addDataSource(authDataSource);
                }

                UserPreferencesRepository userPrefRepo = new UserPreferencesRepository(authDataSource);
                AuthenticationRepository authRepo = new AuthenticationRepository(authDataSource);
                return new AuthenticationViewModel(userPrefRepo, authRepo);
            }
    );

    private final UserPreferencesRepository _userPreferencesRepository;
    private final AuthenticationRepository _authenticationRepository;
    private final ExecutorService executorService;
    private MutableLiveData<AuthenticationUiState> mutableAuthUiState;
    private final boolean hasMasterPassword;
    private final byte maxAuthAttempts;
    private byte authAttempts;

    public AuthenticationViewModel(UserPreferencesRepository userPrefRepo, AuthenticationRepository authRepo) {
        this._userPreferencesRepository = userPrefRepo;
        this._authenticationRepository = authRepo;
        this.executorService = Executors.newSingleThreadExecutor();

        this.hasMasterPassword = ExecutorServiceUtils.executeCallable(this.executorService, userPrefRepo::isMasterPasswordAlreadySet);
        this.maxAuthAttempts = ExecutorServiceUtils.executeCallable(this.executorService, authRepo::getMaxAuthAttempts);

        AuthenticationUiState uiState = new AuthenticationUiState(null, null, false, null,false);
        this.mutableAuthUiState = new MutableLiveData<>(uiState);
        this.authAttempts = 0;
        // FIXME: 16/02/2026 it's not reload when no more activity is shown but the process is running
        System.out.println("max auth attempts : " + this.maxAuthAttempts);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        this.executorService.shutdown();
    }

    public LiveData<AuthenticationUiState> getAuthUiState() {
        return this.mutableAuthUiState;
    }

    public boolean isMasterPasswordExists() {
        return this.hasMasterPassword;
    }

    public void createMasterPassword(String masterPassword) {
        if (!this.hasMasterPassword) {
            AuthenticationUiState newUiState;
            boolean isMasterPasswordSaved = ExecutorServiceUtils.executeCallable(this.executorService, () -> this._userPreferencesRepository.saveMasterPassword(masterPassword));

            if (isMasterPasswordSaved) {
                newUiState = new AuthenticationUiState(masterPassword, masterPassword, true, null, false);
            }
            else {
                newUiState = new AuthenticationUiState(masterPassword, masterPassword, false, R.string.error_master_password_creation, false);
            }
            this.mutableAuthUiState.setValue(newUiState);
        }
    }

    public boolean login(String masterPassword) {
        if (!masterPassword.isBlank()) {
            this.authAttempts++;
            AuthenticationUiState newUiState;
            boolean isAuthenticated = ExecutorServiceUtils.executeCallable(this.executorService, () -> this._authenticationRepository.authenticateUser(masterPassword));

            if (isAuthenticated) {
                newUiState = new AuthenticationUiState(masterPassword, null, true, null, false);
            }
            else {
                boolean maxAttemptsReached = this.authAttempts >= this.maxAuthAttempts;
                //don't show the error message in snack bar but as a textInputLayout error
                //use return of this method
                newUiState = new AuthenticationUiState(masterPassword, null, false, null, maxAttemptsReached);
            }
            this.mutableAuthUiState.setValue(newUiState);
            return isAuthenticated;
        }
        return false;
    }

    public void destroyAllData() {
        ExecutorServiceUtils.executeRunnable(this.executorService, this._authenticationRepository::destroyAllData);
    }
}
