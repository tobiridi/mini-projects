package be.tobiridi.passwordsecurity.ui.activities;

import static androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY;

import android.app.Application;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.viewmodel.ViewModelInitializer;
import androidx.preference.PreferenceManager;

import java.time.Instant;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import be.tobiridi.passwordsecurity.data.datasources.DataSourceProvider;
import be.tobiridi.passwordsecurity.data.datasources.local.SettingsLocalDataSource;
import be.tobiridi.passwordsecurity.data.repositories.SettingsRepository;
import be.tobiridi.passwordsecurity.data.utils.ExecutorServiceUtils;

public class MainViewModel extends ViewModel {
    /*********************/
    /* ViewModel Factory */
    /*********************/
    public static final ViewModelInitializer<MainViewModel> initializer = new ViewModelInitializer<>(
            MainViewModel.class,
            creationExtras -> {
                Application app = creationExtras.get(APPLICATION_KEY);
                assert app != null;

                DataSourceProvider provider = DataSourceProvider.getProvider();
                SettingsLocalDataSource settingsLocalDataSource = provider.getLocalDataSource(SettingsLocalDataSource.class);
                if (settingsLocalDataSource == null) {
                    settingsLocalDataSource = new SettingsLocalDataSource(PreferenceManager.getDefaultSharedPreferences(app));
                    provider.addDataSource(settingsLocalDataSource);
                }

                SettingsRepository settingsRepo = new SettingsRepository(settingsLocalDataSource);
                return new MainViewModel(settingsRepo);
            }
    );

    private final MutableLiveData<MainUiState> mutableMainUiState;
    private final SettingsRepository settingsRepository;
    private final ExecutorService executorService;
    private Instant endAutoDisconnect;

    public MainViewModel(SettingsRepository settingsRepository) {
        this.executorService = Executors.newSingleThreadExecutor();
        this.settingsRepository = settingsRepository;
        MainUiState uiState = new MainUiState(null, null, false);
        this.mutableMainUiState = new MutableLiveData<>(uiState);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        this.executorService.shutdown();
    }

    public LiveData<MainUiState> getMainUiState() {
        return this.mutableMainUiState;
    }

    /**
     * Update the fragment to display.
     */
    public void updateDisplayFragment(Fragment fragment) {
        MainUiState oldUiState = this.mutableMainUiState.getValue();
        MainUiState uiState = new MainUiState(oldUiState.getCurrentFragDisplay(), fragment, true);
        this.mutableMainUiState.setValue(uiState);
    }

    public void setEndAutoDisconnect() {
        int duration = ExecutorServiceUtils.executeCallable(this.executorService, this.settingsRepository::getAutoDisconnectDuration);
        this.endAutoDisconnect = Instant.now().plusSeconds(duration);
    }

    public Instant getDisconnectInstant() {
        return this.endAutoDisconnect;
    }
}
