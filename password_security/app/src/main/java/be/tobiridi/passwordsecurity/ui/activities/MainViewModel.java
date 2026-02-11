package be.tobiridi.passwordsecurity.ui.activities;

import static androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY;

import android.app.Application;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.viewmodel.ViewModelInitializer;

public class MainViewModel extends ViewModel {
    /*********************/
    /* ViewModel Factory */
    /*********************/
    public static final ViewModelInitializer<MainViewModel> initializer = new ViewModelInitializer<>(
            MainViewModel.class,
            creationExtras -> {
                Application app = creationExtras.get(APPLICATION_KEY);
                assert app != null;

                return new MainViewModel();
            }
    );

    private final MutableLiveData<MainUiState> mutableMainUiState;

    public MainViewModel() {
        MainUiState uiState = new MainUiState(null, null, false);
        this.mutableMainUiState = new MutableLiveData<>(uiState);
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
}
